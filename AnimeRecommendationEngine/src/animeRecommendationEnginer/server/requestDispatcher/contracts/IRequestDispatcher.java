package animeRecommendationEnginer.server.requestDispatcher.contracts;

import java.util.Map;

import javax.ejb.Local;

import animeRecommendationEnginer.server.recommendationRequestHandler.properties.RecommendationResponseProperties;

@Local
public interface IRequestDispatcher {

	public RecommendationResponseProperties dispatchRequest(
			Map<String, String> request);

}
