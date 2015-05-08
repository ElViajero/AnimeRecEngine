package animeRecommendationEnginer.server.recommendationRequestHandler.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import animeRecommendationEnginer.server.dbRequestHandler.services.AnimeDBRequestHandler;
import animeRecommendationEnginer.server.recommendationRequestHandler.contracts.IUserRecommendationRequestHandler;

/**
 * 
 * @author tejasvamsingh
 *
 */
@Stateless
public class UserRecommendationRequestHandler implements
		IUserRecommendationRequestHandler {

	@Override
	public double getUserSimilarity(String userOneId, String userTwoId) {
		AnimeDBRequestHandler handler = new AnimeDBRequestHandler();
		List<Map<String,String>> results = handler.getSharedAnime(userOneId, userTwoId);
		//TODO change the function to perform the ranking over all scores rather than the scores for shared anime
		int i;
		Map<String, String> x;
		List<String> userOneRank = new ArrayList<String>();
		List<String> userTwoRank = new ArrayList<String>();
		for(i = 0; i < results.size(); i++) {
			if(!(userOneRank.contains(results.get(i).get("user1Rating")))) {
				userOneRank.add(results.get(i).get("user1Rating"));
			}
			if(!(userTwoRank.contains(results.get(i).get("user2Rating")))) {
				userTwoRank.add(results.get(i).get("user2Rating"));
			}
		}
		Collections.sort(userOneRank);
		Collections.sort(userTwoRank);
		double hits = 0;
		double cases = 0;
		int j;
		for(i = 0; i < results.size();i++) {
			for(j=i;j<results.size();j++) {
				int dif1 = userOneRank.indexOf(results.get(i).get("user1Rating")) - userOneRank.indexOf(results.get(j).get("user1Rating"));
				int dif2 = userTwoRank.indexOf(results.get(i).get("user2Rating")) - userTwoRank.indexOf(results.get(j).get("user2Rating"));
				if((dif1 > 0 && dif2 > 0) || (dif1 == 0 && dif2 == 0) || (dif1 < 0 && dif2 < 0)) {
					hits++;
					cases++;
				}
				else if(dif1 == 0 && Math.abs(dif2) == 1 || dif2 == 0 && Math.abs(dif1) == 1) {
					hits += 0.5;
					cases ++;
				}
				else
				{
					cases++;
				}
			}
		}
		return hits/cases;
	}

}
