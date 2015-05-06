package animeRecommendationEnginer.server.dbRequestHandler.services;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import animeRecommendationEnginer.server.dbRequestHandler.contracts.IAnimeDBRequestHandler;

/**
 * This class handles anime persistence operations.
 * 
 * @author tejasvamsingh
 *
 */
@Stateless
public class AnimeDBRequestHandler implements IAnimeDBRequestHandler {

	@Override
	public List<Map<String, String>> getWatchedAnime(
			Map<String, String> requestMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, String>> updateWatchedAnime(
			Map<String, String> requestMap) {
		// TODO Auto-generated method stub
		return null;
	}

}
