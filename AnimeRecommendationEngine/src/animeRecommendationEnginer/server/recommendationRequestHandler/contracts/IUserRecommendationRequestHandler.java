package animeRecommendationEnginer.server.recommendationRequestHandler.contracts;

import javax.ejb.Local;

/**
 * 
 * @author tejasvamsingh
 *
 */
@Local
public interface IUserRecommendationRequestHandler {

	public double getUserSimilarity(String userOneId, String userTwoId);

}
