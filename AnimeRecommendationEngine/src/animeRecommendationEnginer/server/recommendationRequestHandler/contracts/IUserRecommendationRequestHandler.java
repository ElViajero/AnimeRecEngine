package animeRecommendationEnginer.server.recommendationRequestHandler.contracts;

import java.util.Map;

import javax.ejb.Local;

import animeRecommendationEnginer.server.recommendationRequestHandler.properties.RecommendationResponseProperties;

/**
 * 
 * @author tejasvamsingh
 *
 */
@Local
public interface IUserRecommendationRequestHandler {

	public double getUserSimilarity(String userOneId, String userTwoId);

	public RecommendationResponseProperties getSimilarUsers(
			Map<String, String> requestMap);

}
