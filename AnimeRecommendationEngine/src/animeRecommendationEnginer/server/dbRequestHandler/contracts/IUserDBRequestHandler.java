package animeRecommendationEnginer.server.dbRequestHandler.contracts;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
			Set<String> userIdList);

	public boolean updateUserSimilarity(String userIdOne, String userIdTwo,
			double score);
	
	public List<Map<String, String>> getRatedAnimeFromSimilarUsers(String userId);

}