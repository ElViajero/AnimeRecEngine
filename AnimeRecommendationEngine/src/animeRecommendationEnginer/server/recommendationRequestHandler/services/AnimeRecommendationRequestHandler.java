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
import animeRecommendationEnginer.server.recommendationRequestHandler.contracts.IAnimeRecommendationRequestHandler;
import animeRecommendationEnginer.server.recommendationRequestHandler.contracts.IUserRecommendationRequestHandler;
import animeRecommendationEnginer.server.recommendationRequestHandler.helper.RequestExecutor;
import animeRecommendationEnginer.server.recommendationRequestHandler.helper.ScoreComparator;
import animeRecommendationEnginer.server.recommendationRequestHandler.properties.RecommendationResponseProperties;
import animeRecommendationEnginer.server.reflectionManager.contracts.IReflectionManager;

import com.google.gson.Gson;

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
		System.out.println("response is :" + new Gson().toJson(response));
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

		System.out.println("size of list :" + recommendedAnimeMapList.size());
		System.out.println("list :" + recommendedAnimeMapList);
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

		System.out.println("HTML Source list is : " + htmlSource);

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

		// getRatedAnimeFromSimilarUsers

		// getting all the ratings that fulfill the 5 shared anime.
		List<Map<String, String>> recList = null; // getSimilarities(userId);

		Set<String> userIds = new HashSet<String>();

		// get a set of users
		for (Map<String, String> rec : recList)
			userIds.add(rec.get("userId"));

		// Get the mean and std. dev.

		Map<String, Map<String, Double>> weightedInfoMap = null; // getStats(userIds);

		Map<String, Map<String, Double>> predictedScoreInfoMap = new HashMap<String, Map<String, Double>>();
		double userMean = weightedInfoMap.get(userId).get("mean");
		double userDev = weightedInfoMap.get(userId).get("stdDev");

		for (Map<String, String> rec : recList) {1
			if (!predictedScoreInfoMap.containsKey(rec.get("animeId"))) {
				Map<String, Double> temp = new HashMap<String, Double>();
				temp.put("weight", 0.0);
				temp.put("scoreSum", 0.0);

				// TODO other information such as animeTitle, animeLink etc.
				predictedScoreInfoMap.put(rec.get("animeId"), temp);
			}

			// Get the information of the user recommending anime

			double mean = weightedInfoMap.get(rec.get("userId")).get("mean");
			double stdDev = weightedInfoMap.get(rec.get("userId"))
					.get("stdDev");

			double tempSim = new Double(rec.get("similarity")) - 0.5;
			double distance = (mean - new Double(rec.get("animeRating")))
					/ stdDev * Math.signum(tempSim);

			// calculate the new score
			double newScore = userMean - distance * userDev;
			newScore = Math.min(10, Math.max(1, newScore));
			double insertScore = newScore
					* Math.abs(tempSim)
					+ predictedScoreInfoMap.get(rec.get("animeId")).get(
							"scoreSum");

			// put the new scores and weight in
			predictedScoreInfoMap.get(rec.get("animeId")).put("scoreSum",
					insertScore);
			double insertWeight = Math.abs(tempSim)
					+ predictedScoreInfoMap.get(rec.get("animeId")).get(
							"weight");
			predictedScoreInfoMap.get(rec.get("animeId")).put("weight",
					insertWeight);

			// calculate a score and add it to the map
			double score = insertScore / insertWeight;
			predictedScoreInfoMap.get("animeId").put("score", score);
		}

		// create a content map list to add to
		// our reponse.
		List<Map<String, String>> contentMapList = new ArrayList<Map<String, String>>();

		for (String key : predictedScoreInfoMap.keySet()) {
			Map<String, String> returnMap = new HashMap<String, String>();
			for (Map.Entry<String, Double> entry : predictedScoreInfoMap.get(
					key).entrySet()) {
				String keyString = entry.getKey();
				String valString = String.valueOf(entry.getValue());
				returnMap.put(keyString, valString);
			}

			returnMap.put("animeId", key);
			contentMapList.add(returnMap);
		}

		// sort the list
		Collections.sort(contentMapList, new ScoreComparator());
		// add it to the response
		response.setContentList(contentMapList);

		return response;
	}
}
