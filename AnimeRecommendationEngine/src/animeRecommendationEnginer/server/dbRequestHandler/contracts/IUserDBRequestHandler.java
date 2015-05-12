package animeRecommendationEnginer.server.dbRequestHandler.contracts;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

/**
 * This interface defines the contract for user persistence operations.
 * 
 * 
 * @author tejasvamsingh
 *
 */

@Local
public interface IUserDBRequestHandler {

	public Map<String, Map<String, Double>> getUserAnimeStats(
			List<String> userIdList);

}
