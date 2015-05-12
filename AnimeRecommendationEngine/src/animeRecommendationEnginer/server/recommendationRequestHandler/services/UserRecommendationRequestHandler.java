package animeRecommendationEnginer.server.recommendationRequestHandler.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import animeRecommendationEnginer.server.dbRequestHandler.contracts.IAnimeDBRequestHandler;
import animeRecommendationEnginer.server.recommendationRequestHandler.contracts.IUserRecommendationRequestHandler;

/**
 * 
 * @author tejasvamsingh
 * 
 *
 */
@Stateless
public class UserRecommendationRequestHandler implements
		IUserRecommendationRequestHandler {

	@Inject
	IAnimeDBRequestHandler iAnimeDBRequestHandler;

	@Override
	public double getUserSimilarity(String userOneId, String userTwoId) {

		List<Map<String, String>> results = iAnimeDBRequestHandler
				.getSharedAnime(userOneId, userTwoId);

		// TODO change the function to perform the ranking over all scores
		// rather than the scores for shared anime
		int i;
		Map<String, String> x;
		List<String> userOneRank = new ArrayList<String>();
		List<String> userTwoRank = new ArrayList<String>();
		for (i = 0; i < results.size(); i++) {
			if (!(userOneRank.contains(results.get(i).get("user1Rating")))) {
				userOneRank.add(results.get(i).get("user1Rating"));
			}
			if (!(userTwoRank.contains(results.get(i).get("user2Rating")))) {
				userTwoRank.add(results.get(i).get("user2Rating"));
			}
		}

		// sort unique scale
		Collections.sort(userOneRank);
		Collections.sort(userTwoRank);

		double hits = 0;
		double cases = 0;
		int j;

		// OPTIMIZE THIS !!!!!!!

		for (i = 0; i < results.size(); i++) {
			for (j = i + 1; j < results.size(); j++) {
				int dif1 = userOneRank.indexOf(results.get(i)
						.get("user1Rating"))
						- userOneRank
								.indexOf(results.get(j).get("user1Rating"));
				int dif2 = userTwoRank.indexOf(results.get(i)
						.get("user2Rating"))
						- userTwoRank
								.indexOf(results.get(j).get("user2Rating"));
				// TODO if both of them have given diff zero, then
				// skip.
				if (dif1 == 0 && dif2 == 0)
					continue;
				if ((dif1 > 0 && dif2 > 0) || (dif1 < 0 && dif2 < 0)) {
					hits++;
				} else if (dif1 == 0 && Math.abs(dif2) == 1 || dif2 == 0
						&& Math.abs(dif1) == 1) {
					hits += 0.5;
				}
				cases++;
			}
		}
		return hits / cases;
	}
}
