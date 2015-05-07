package animeRecommendationEnginer.server.recommendationRequestHandler.services;

import javax.ejb.Stateless;

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

		// TODO something more interesting.
		return 1;
	}

}
