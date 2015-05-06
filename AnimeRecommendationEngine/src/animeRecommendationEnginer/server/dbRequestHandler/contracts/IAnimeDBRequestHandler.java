package animeRecommendationEnginer.server.dbRequestHandler.contracts;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

/**
 * This interface defines the contract for anime related persistence operations.
 * 
 * @author tejasvamsingh
 *
 */
@Local
public interface IAnimeDBRequestHandler {

	public List<Map<String, String>> getWatchedAnime(
			Map<String, String> requestMap);

	public List<Map<String, String>> updateWatchedAnime(
			Map<String, String> requestMap);

}
