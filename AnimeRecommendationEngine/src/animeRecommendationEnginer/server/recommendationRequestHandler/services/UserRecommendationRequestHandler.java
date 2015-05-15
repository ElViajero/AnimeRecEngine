package animeRecommendationEnginer.server.recommendationRequestHandler.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import animeRecommendationEnginer.server.dbRequestHandler.contracts.IAnimeDBRequestHandler;
import animeRecommendationEnginer.server.dbRequestHandler.contracts.IUserDBRequestHandler;
import animeRecommendationEnginer.server.recommendationRequestHandler.contracts.IUserRecommendationRequestHandler;

/**
 * 
 * @author laurencegoldinger
 * @author tejasvamsingh
 * 
 *
 */
@Stateless
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

				int userOnerankingDifference = userOneRankList.indexOf(results
						.get(i).get("userOneRating"))
						- userOneRankList.indexOf(results.get(j).get(
								"userOneRating"));

				int userTwoRankingDifference = userTwoRankList.indexOf(results
						.get(i).get("userTwoRating"))
						- userTwoRankList.indexOf(results.get(j).get(
								"userTwoRating"));

				if (userOnerankingDifference == 0
						&& userTwoRankingDifference == 0
						/*|| (userOnerankingDifference == 0 && 
						Math.abs(userOnerankingDifference) < 1)|| (userTworankingDifference == 0 && Math.abs(userOnerankingDifference) < 1) */)
					continue;
				if ((userOnerankingDifference > 0 && userTwoRankingDifference > 0)
						|| (userOnerankingDifference < 0 && userTwoRankingDifference < 0))
					hits++;
				else if (userOnerankingDifference == 0
						&& Math.abs(userTwoRankingDifference) == 1
						|| userTwoRankingDifference == 0
						&& Math.abs(userOnerankingDifference) == 1)
					hits += 0.5;

				cases++;
			}
		}

		// calculate the score
		double score = hits / cases;
		// persist it.
		iUserDBRequestHandler.updateUserSimilarity(userOneId, userTwoId, score);

		return hits / cases;
	}
}
