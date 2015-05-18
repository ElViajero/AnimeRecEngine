package animeRecommendationEnginer.server.recommendationRequestHandler.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;

import animeRecommendationEnginer.server.dbRequestHandler.contracts.IAnimeDBRequestHandler;
import animeRecommendationEnginer.server.dbRequestHandler.contracts.IUserDBRequestHandler;
import animeRecommendationEnginer.server.fileWriter.contracts.IFileWriter;
import animeRecommendationEnginer.server.recommendationRequestHandler.contracts.IAnimeRecommendationRequestHandler;
import animeRecommendationEnginer.server.recommendationRequestHandler.contracts.IUserRecommendationRequestHandler;
import animeRecommendationEnginer.server.recommendationRequestHandler.helper.RequestExecutor;
import animeRecommendationEnginer.server.recommendationRequestHandler.helper.ScoreComparator;
import animeRecommendationEnginer.server.recommendationRequestHandler.properties.RecommendationResponseProperties;
import animeRecommendationEnginer.server.reflectionManager.contracts.IReflectionManager;

/**
 * This class handles anime recommendations.
 * 
 * @author tejasvamsingh
 *
 */
public class AnimeRecommendationRequestHandler implements
		IAnimeRecommendationRequestHandler {

	final String prefixString = "animeRecommendationEnginer.server.htmlParser.services.";
	final String suffixString = "HTMLParser";
	final String baseUrlString = "http://myanimelist.net";

	@Inject
	IReflectionManager iReflectionManager;
	@Inject
	RequestExecutor requestExecutor;
	@Inject
	IAnimeDBRequestHandler iAnimeDBRequestHandler;
	@Inject
	IUserRecommendationRequestHandler iUserRecommendationRequestHandler;

	@Inject
	IUserDBRequestHandler iUserDBRequestHandler;

	@Inject
	IFileWriter iFilerWriter;

	/**
	 * @author tejasvamsingh
	 */
	@Override
	public RecommendationResponseProperties getSimilar(
			Map<String, String> requestMap) {

		System.out
				.println("reaching getSimilar in AnimeRecommendationRequestHandler");

		// get the recommended anime list.
		RecommendationResponseProperties response = getRecommendedAnime(requestMap);
		if (response.getSuccess().equalsIgnoreCase("false"))
			return response;
		List<Map<String, String>> recommendedAnimeMapList = response
				.getContentList();

		// select 5 entries
		Random random = new Random();
		Set<Integer> entryIndexSet = new HashSet<Integer>();
		while (entryIndexSet.size() < 5)
			entryIndexSet.add(random.nextInt(recommendedAnimeMapList.size()));

		// create a prunnedMap
		List<Map<String, String>> prunnedRecommendationAnimeMapList = new ArrayList<Map<String, String>>();
		for (Integer index : entryIndexSet) {
			prunnedRecommendationAnimeMapList.add(recommendedAnimeMapList
					.get(index));
		}

		for (Map<String, String> animeEntry : prunnedRecommendationAnimeMapList) {
			String userTwoId = animeEntry.get("recommenderId");
			// make sure the users anime list is in the database.
			Map<String, String> newRequestMap = new HashMap<String, String>();
			newRequestMap.put("userId", userTwoId);
			getWatched(newRequestMap);
			// get the similarity
			animeEntry.put("score", String
					.valueOf(iUserRecommendationRequestHandler
							.getUserSimilarity(requestMap.get("userId"),
									userTwoId)));
		}

		Collections.sort(prunnedRecommendationAnimeMapList,
				new ScoreComparator());
		response.setContentList(prunnedRecommendationAnimeMapList);

		return response;
	}

	/**
	 * 
	 * 
	 * @author tejasvamsingh
	 * @param requestMap
	 * @return
	 */
	private RecommendationResponseProperties getRecommendedAnime(
			Map<String, String> requestMap) {

		RecommendationResponseProperties response = new RecommendationResponseProperties();

		// check the DB for similar anime.
		List<Map<String, String>> recommendedAnimeMapList = iAnimeDBRequestHandler
				.getRecommendedAnime(requestMap.get("animeId"));

		// if not successful, we need to fetch
		if (recommendedAnimeMapList.get(0).get("Status").equals("Failed")) {

			// get the link of the given anime
			String animeLink = "";
			List<Map<String, String>> animeMapList = iAnimeDBRequestHandler
					.getAnime(requestMap.get("animeId"));

			if (animeMapList.get(0).get("Status").equals("Success")) {
				animeLink = animeMapList.get(1).get("animeLink");
			} else {
				response.setErrorMessage("Anime is not in the database. Anime should be selected based on user's watched list.");
				return response;
			}

			// fetch the recommended list
			String urlString = baseUrlString + animeLink + "/userrecs";
			recommendedAnimeMapList = fetchList(urlString,
					"UserRecommendations");

			if (recommendedAnimeMapList.size() == 0) {
				response.setErrorMessage("Could not fetch any recommended anime");
				return response;
			}

			// persist it to the db
			iAnimeDBRequestHandler.updateAnimeRecommendations(
					recommendedAnimeMapList, requestMap.get("animeId"));
		} else {// we found it in the db. remove the status map
			recommendedAnimeMapList.remove(0);
		}

		response.setContentList(recommendedAnimeMapList);
		response.setSuccess("true");
		return response;
	}

	/**
	 * This method returns a list of watched anime for a given user.
	 * 
	 * @author tejasvamsingh
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public RecommendationResponseProperties getWatched(
			Map<String, String> requestMap) {

		System.out
				.println("reaching getWatched in AnimeRecommendationRequestHandler");
		RecommendationResponseProperties recommendationResponse = new RecommendationResponseProperties();
		if (!requestMap.containsKey("userId")) {
			recommendationResponse
					.setErrorMessage("You have not supplied an username.");
		} else {// we're in business.

			// check the DB for list.
			List<Map<String, String>> watchedAnimeMapList = iAnimeDBRequestHandler
					.getWatchedAnime(requestMap.get("userId"));

			// remove the status map in the case of successful fetch.
			if (watchedAnimeMapList.get(0).get("Status").equals("Success"))
				watchedAnimeMapList.remove(0);
			// otherwise fetch it.
			else {
				String urlString = baseUrlString + "/animelist/"
						+ requestMap.get("userId");
				watchedAnimeMapList = fetchList(urlString, "AnimeList");

				// persist it to the db
				iAnimeDBRequestHandler.updateWatchedAnime(watchedAnimeMapList,
						requestMap.get("userId"));
			}

			if (!watchedAnimeMapList.isEmpty()) {
				// set up the response
				recommendationResponse.setContentList(watchedAnimeMapList);
				recommendationResponse.setSuccess("true");
			}

		}

		return recommendationResponse;
	}

	/**
	 * This method fetches from the website if results are not available in the
	 * DB.
	 * 
	 * @author tejasvamsingh
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, String>> fetchList(String urlString,
			String htmlParserString) {
		String htmlSource = requestExecutor.getHTMLSource(urlString);

		// parse the content
		Object classObject = iReflectionManager
				.getMyBeanFromClassName(prefixString + htmlParserString
						+ suffixString);

		// get the map back.
		List<Map<String, String>> watchedAnimeMapList = (List<Map<String, String>>) iReflectionManager
				.invokeMethod(classObject, "parseHtml", htmlSource, "String");

		return watchedAnimeMapList;

	}

	/**
	 * GET RATINGS FOR SHOWS THAT I HAVE NOT WATCHED BUT OTHER USERS WHO SHARE
	 * AT LEAST 5 ANIME WITH ME HAVE SEEN.
	 * 
	 */
	@Override
	public RecommendationResponseProperties getWeightedAnimePredictions(
			Map<String, String> requestMap) {

		String userId = requestMap.get("userId");
		RecommendationResponseProperties response = new RecommendationResponseProperties();
		response.setErrorMessage("No rated anime from similar users found.");

		// getRatedAnimeFromSimilarUsers

		// getting all the ratings that fulfill the 5 shared anime.
		List<Map<String, String>> recList = iUserDBRequestHandler
				.getRatedAnimeFromSimilarUsers(userId);

		Set<String> userIds = new HashSet<String>();
		if (recList.get(0).get("Status").equals("Failed"))
			return response;

		recList.remove(0);
		// get a set of users
		for (Map<String, String> rec : recList)
			userIds.add(rec.get("userId"));
		userIds.add(userId);

		// Get the mean and std. dev.

		Map<String, Map<String, Double>> weightedInfoMap = iUserDBRequestHandler
				.getUserAnimeStats(userIds);

		Map<String, Map<String, String>> predictedScoreInfoMap = new HashMap<String, Map<String, String>>();

		double userMean = 0;
		double userDev = 0;

		try {
			userMean = weightedInfoMap.get(userId).get("mean");
			userDev = weightedInfoMap.get(userId).get("stdDev");
		} catch (NullPointerException e) {

		}

		for (Map<String, String> rec : recList) {
			String animeId = rec.get("animeId");

			if (!predictedScoreInfoMap.containsKey(rec.get("animeId"))) {
				Map<String, String> currentAnimeInfoMap = new HashMap<String, String>();
				currentAnimeInfoMap.put("weight", "0.0");
				currentAnimeInfoMap.put("scoreSum", "0.0");
				predictedScoreInfoMap.put(animeId, currentAnimeInfoMap);
			}

			double mean = 0;
			double stdDev = 0;
			// Get the information of the user recommending anime
			try {
				mean = weightedInfoMap.get(rec.get("userId")).get("mean");
				stdDev = weightedInfoMap.get(rec.get("userId")).get("stdDev");
			} catch (NullPointerException e) {

				// System.out.println("The weightedInfoMap is : "
				// + weightedInfoMap);
				// System.out.flush();

			}

			double tempSim = Double.parseDouble(rec.get("similarityScore")) - 0.5;
			int tempSimSign = (int) Math.signum(tempSim);
			tempSim = Math.pow(tempSim, 8);
			tempSim *= tempSimSign;

			double distance = 0;
			try {
				distance = (mean - new Double(rec.get("userAnimeRating")))
						/ stdDev * Math.signum(tempSim);
			} catch (NullPointerException e) {

			}

			// if (printFlag == true) {
			// printFlag = false;
			// System.out.println("tempSim is : " + tempSim);
			// System.out.println("distance is : " + distance);
			// System.out.println("mean is : " + distance);
			// System.out.println("stdDev is : " + stdDev);
			// System.out.println("mean is : " + mean);
			// System.out.println("newScore is : "+ newScore);
			// }

			// calculate the new score
			double newScore = userMean - distance * userDev;
			// newScore = Math.min(10, Math.max(1, newScore));
			double insertScore = newScore
					* Math.abs(tempSim)
					+ Double.parseDouble(predictedScoreInfoMap.get(
							rec.get("animeId")).get("scoreSum"));

			// put the new scores and weight in
			predictedScoreInfoMap.get(rec.get("animeId")).put("scoreSum",
					String.valueOf(insertScore));

			double insertWeight = Math.abs(tempSim)
					+ Double.parseDouble(predictedScoreInfoMap.get(
							rec.get("animeId")).get("weight"));
			predictedScoreInfoMap.get(rec.get("animeId")).put("weight",
					String.valueOf(insertWeight));

			// if (insertWeight < 1)
			// continue;

			// calculate a total score and add it to the map

			double score = insertScore / insertWeight;
			score = Math.min(score, 10);
			try {
				String myAnimeRating = rec.get("myAnimeRating");
				predictedScoreInfoMap.get(animeId).put("score",
						String.valueOf(score));
				predictedScoreInfoMap.get(animeId).put("myAnimeRating",
						myAnimeRating);
			} catch (NullPointerException e) {
				// System.out.println("The predictedScoreInfoMap is : "
				// + predictedScoreInfoMap);

			}

		}

		// create a content map list to add to
		// our response.
		List<Map<String, String>> contentMapList = new ArrayList<Map<String, String>>();

		for (String key : predictedScoreInfoMap.keySet()) {
			Map<String, String> returnMap = new HashMap<String, String>();
			for (Map.Entry<String, String> entry : predictedScoreInfoMap.get(
					key).entrySet()) {
				String keyString = entry.getKey();
				String valString = String.valueOf(entry.getValue());
				returnMap.put(keyString, valString);
			}
			List<Map<String, String>> animeInfoMapList = iAnimeDBRequestHandler
					.getAnime(key);
			if (animeInfoMapList.size() < 2)
				continue;
			returnMap.put("animeId", key);
			returnMap.putAll(iAnimeDBRequestHandler.getAnime(key).get(1));
			contentMapList.add(returnMap);
		}

		// sort the list
		Collections.sort(contentMapList, new ScoreComparator());

		// add it to the response
		response.setContentList(contentMapList);

		// System.out.flush();
		response.setSuccess("true");

		System.out.println("ContentMapList is : " + contentMapList);

		// Write the contentMapList to file.
		iFilerWriter.writePredictedScoreToFile(contentMapList);

		return response;
	}
}
