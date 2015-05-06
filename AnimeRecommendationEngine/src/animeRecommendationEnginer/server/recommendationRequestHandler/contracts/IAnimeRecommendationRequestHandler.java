package animeRecommendationEnginer.server.recommendationRequestHandler.contracts;

import java.util.Map;

import javax.ejb.Local;

import animeRecommendationEnginer.server.recommendationRequestHandler.properties.RecommendationResponseProperties;

/**
 * This interface defines the contract for anime recommendation based
 * operations.
 * 
 * @author tejasvamsingh
 *
 */
@Local
public interface IAnimeRecommendationRequestHandler {

	public RecommendationResponseProperties getSimilar(
			Map<String, String> requestMap);

	public RecommendationResponseProperties getWatched(
			Map<String, String> requestMap);
}
