package animeRecommendationEnginer.server.recommendationRequestHandler.services;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import animeRecommendationEnginer.server.dbRequestHandler.contracts.IAnimeDBRequestHandler;
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
	@Inject
	IAnimeDBRequestHandler iAnimeDBRequestHandler;

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
		RecommendationResponseProperties recommendationResponse = new RecommendationResponseProperties();
		if (!requestMap.containsKey("userId")) {
			recommendationResponse
					.setErrorMessage("You have not supplied an username.");
		} else {// we're in business.

			// TODO check the DB here
			List<String> animeListHTMLSourceList = requestExecutor
					.getHTMLSource("http://myanimelist.net/animelist/"
							+ requestMap.get("userId"));

			System.out.println("HTML Source list is : "
					+ animeListHTMLSourceList);

			// parse the content
			Object classObject = iReflectionManager
					.getMyBeanFromClassName(prefixString + "AnimeList"
							+ suffixString);

			// get the map back.
			List<Map<String, String>> watchedAnimeMapList = (List<Map<String, String>>) iReflectionManager
					.invokeMethod(classObject, "parseHtml",
							animeListHTMLSourceList, "List");

			if (!watchedAnimeMapList.isEmpty()) {

				// persist it to the db
				iAnimeDBRequestHandler.updateWatchedAnime(watchedAnimeMapList,
						requestMap.get("userId"));
				// set up the response
				recommendationResponse.setContentList(watchedAnimeMapList);
				recommendationResponse.setSuccess("true");
			}

		}

		return recommendationResponse;
	}
}
