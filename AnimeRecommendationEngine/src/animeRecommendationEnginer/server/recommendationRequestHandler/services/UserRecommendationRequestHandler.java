package animeRecommendationEnginer.server.recommendationRequestHandler.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import animeRecommendationEnginer.server.dbRequestHandler.contracts.IAnimeDBRequestHandler;
import animeRecommendationEnginer.server.dbRequestHandler.contracts.IUserDBRequestHandler;
import animeRecommendationEnginer.server.recommendationRequestHandler.contracts.IUserRecommendationRequestHandler;
import animeRecommendationEnginer.server.recommendationRequestHandler.properties.RecommendationResponseProperties;

/**
 * 
 * @author laurencegoldinger
 * @author tejasvamsingh
 * 
 *
 */

public class UserRecommendationRequestHandler implements
		IUserRecommendationRequestHandler {

	@Inject
	IAnimeDBRequestHandler iAnimeDBRequestHandler;
	@Inject
	IUserDBRequestHandler iUserDBRequestHandler;

	/**
	 * 
	 * @author laurencegoldinger
	 * @author tejasvamsingh
	 * 
	 */
	@Override
	public double getUserSimilarity(String userOneId, String userTwoId) {

		// get the shared anime.
		List<Map<String, String>> results = iAnimeDBRequestHandler
				.getSharedAnime(userOneId, userTwoId);

		System.out.println("results is : " + results);

		// It's possible that there are no shared anime.
		if (results.get(0).get("Status").equals("Failed"))
			return 0;

		// remove the status map
		results.remove(0);

		List<String> userOneRankList = new ArrayList<String>();
		List<String> userTwoRankList = new ArrayList<String>();

		// add unique ratings to the rank lists.
		for (Map<String, String> sharedAnimeMap : results) {

			String userOneRatingString = sharedAnimeMap.get("userOneRating");
			String userTwoRatingString = sharedAnimeMap.get("userTwoRating");
			// If the anime is unrated then ignore it

			if (userOneRatingString.equals("-")
					|| userTwoRatingString.equals("-"))
				continue;

			if (!userOneRankList.contains(sharedAnimeMap.get("userOneRating")))
				userOneRankList.add(sharedAnimeMap.get("userOneRating"));

			if (!(userTwoRankList.contains(sharedAnimeMap.get("userTwoRating"))))
				userTwoRankList.add(sharedAnimeMap.get("userTwoRating"));
		}

		System.out.println("userOneRankList is : " + userOneRankList);
		System.out.println("userTwoRankList is : " + userTwoRankList);

		// check here if we actually have some ratings
		if (userOneRankList.isEmpty() || userTwoRankList.isEmpty())
			return 0;

		// sort the rank lists
		Collections.sort(userOneRankList);
		Collections.sort(userTwoRankList);

		double hits = 0;
		double cases = 0;

		for (int i = 0; i < results.size(); i++) {
			for (int j = i + 1; j < results.size(); j++) {

				int userOneRankingDifference = userOneRankList.indexOf(results
						.get(i).get("userOneRating"))
						- userOneRankList.indexOf(results.get(j).get(
								"userOneRating"));

				int userTwoRankingDifference = userTwoRankList.indexOf(results
						.get(i).get("userTwoRating"))
						- userTwoRankList.indexOf(results.get(j).get(
								"userTwoRating"));

				if ((userOneRankingDifference == 0 && Math
						.abs(userOneRankingDifference) <= 1)
						|| (userTwoRankingDifference == 0 && Math
								.abs(userOneRankingDifference) <= 1))

					continue;
				if ((userOneRankingDifference > 0 && userTwoRankingDifference > 0)
						|| (userOneRankingDifference < 0 && userTwoRankingDifference < 0))
					hits++;
				cases++;
			}
		}

		// calculate the score
		double score = hits / cases;
		// persist it.
		iUserDBRequestHandler.updateUserSimilarity(userOneId, userTwoId, score);

		return hits / cases;
	}

	@Override
	public RecommendationResponseProperties getSimilarUsers(
			Map<String, String> requestMap) {

		System.out.println("inside get similar users");
		System.out.flush();
		RecommendationResponseProperties response = new RecommendationResponseProperties();
		response.setErrorMessage("No similar users found.");
		List<Map<String, String>> contentList = new ArrayList<Map<String, String>>();
		String myUserId = requestMap.get("userId");
		// TODO Get users who share at least 5 rated anime with you
		List<String> userIdList = iUserDBRequestHandler
				.getSimilarUsers(myUserId);

		// TODO For each user
		for (String userId : userIdList) {
			// TODO estimate similarity
			double userSimilarityScore = getUserSimilarity(userId, myUserId);
			// TODO persist the user similarity
			iUserDBRequestHandler.updateUserSimilarity(userId, myUserId,
					userSimilarityScore);
			iUserDBRequestHandler.updateUserSimilarity(myUserId, userId,
					userSimilarityScore);
			Map<String, String> currentUserSimilarityMap = new HashMap<String, String>();
			currentUserSimilarityMap.put("myUserId", myUserId);
			currentUserSimilarityMap.put("userId", userId);
			currentUserSimilarityMap.put("score",
					String.valueOf(userSimilarityScore));

			contentList.add(currentUserSimilarityMap);

		}
		response.setContentList(contentList);
		response.setSuccess("true");
		return response;
	}
}
