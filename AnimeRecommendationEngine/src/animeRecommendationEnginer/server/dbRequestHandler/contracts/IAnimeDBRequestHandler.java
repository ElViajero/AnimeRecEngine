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

	public List<Map<String, String>> getWatchedAnime(String userId);

	public boolean updateWatchedAnime(List<Map<String, String>> requestMap,
			String userId);

	public boolean updateAnime(List<Map<String, String>> requestMapList);

}
