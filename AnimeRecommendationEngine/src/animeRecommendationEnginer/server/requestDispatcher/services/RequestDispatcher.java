package animeRecommendationEnginer.server.requestDispatcher.services;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import animeRecommendationEnginer.server.recommendationRequestHandler.properties.RecommendationResponseProperties;
import animeRecommendationEnginer.server.reflectionManager.contracts.IReflectionManager;
import animeRecommendationEnginer.server.requestDispatcher.contracts.IRequestDispatcher;

/**
 * 
 * This class figures out which recommendation handler will handle the request.
 * 
 * @author tejasvamsingh
 *
 */
@Stateless
public class RequestDispatcher implements IRequestDispatcher {

	final String prefixString = "animeRecommendationEnginer.server.recommendationRequestHandler.services.";
	final String suffixString = "RecommendationRequestHandler";

	@Inject
	IReflectionManager iReflectionManager;

	@Override
	public RecommendationResponseProperties dispatchRequest(
			Map<String, String> requestMap) {

		
		try {

			// get the class object
			Object classObject = iReflectionManager
					.getMyBeanFromClassName(prefixString
							+ requestMap.get("requestId") + suffixString);

			// get the method
			return (RecommendationResponseProperties) iReflectionManager
					.invokeMethod(classObject, requestMap.get("requestType"),
							requestMap, "Map");

		} catch (Exception e) {
			return null;
		}

	}
}
