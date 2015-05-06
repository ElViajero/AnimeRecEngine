package animeRecommendationEnginer.server.recommendationRequestHandler.services;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import animeRecommendationEnginer.server.recommendationRequestHandler.contracts.IAnimeRecommendationRequestHandler;
import animeRecommendationEnginer.server.recommendationRequestHandler.helper.RequestExecutor;
import animeRecommendationEnginer.server.recommendationRequestHandler.properties.RecommendationResponseProperties;
import animeRecommendationEnginer.server.reflectionManager.contracts.IReflectionManager;

/**
 * This class handles anime recommedations.
 * 
 * @author tejasvamsingh
 *
 */
public class AnimeRecommendationRequestHandler implements
		IAnimeRecommendationRequestHandler {

	final String prefixString = "animeRecommendationEnginer.server.htmlParser.services.";
	final String suffixString = "HTMLParser";

	@Inject
	IReflectionManager iReflectionManager;

	@Inject
	RequestExecutor requestExecutor;

	@Override
	public RecommendationResponseProperties getSimilar(
			Map<String, String> requestMap) {
		System.out
				.println("reaching getSimilar in AnimeRecommendationRequestHandler");
		return null;
	}

	/**
	 * This method returns a list of watched anime for a given user.
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public RecommendationResponseProperties getWatched(
			Map<String, String> requestMap) {

		System.out
				.println("reaching getWatched in AnimeRecommendationRequestHandler");

		if (!requestMap.containsKey("userId")) {
			RecommendationResponseProperties recommendationResponse = new RecommendationResponseProperties();
			recommendationResponse
					.setErrorMessage("You have not supplied an username.");
		} else {// we're in business.

			// TODO check the DB here
			List<String> animeListHTMLSourceList = requestExecutor
					.getHTMLSource("http://myanimelist.net/animelist/"
							+ requestMap.get("userId"));
			// parse the content
			Object classObject = iReflectionManager
					.getMyBeanFromClassName(prefixString + "AnimeList"
							+ suffixString);

			// get the map back.
			List<Map<String, String>> returnVal = (List<Map<String, String>>) iReflectionManager
					.invokeMethod(classObject, "parseHtml",
							animeListHTMLSourceList, "List");

			System.out.println("return map is : " + returnVal);

		}

		return null;
	}
}
