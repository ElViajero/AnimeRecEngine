package animeRecommendationEnginer.server.dbRequestHandler.services;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import animeRecommendationEnginer.server.DBManager.contracts.IDBRequestExecutor;
import animeRecommendationEnginer.server.dbRequestHandler.contracts.IUserDBRequestHandler;

/**
 * 
 * This class implements user persistence operations.
 * 
 * @author tejasvamsingh
 *
 */

@Stateless
public class UserDBRequestHandler implements IUserDBRequestHandler {

	@Inject
	IDBRequestExecutor iDBRequestExecutor;

	@Override
	public Map<String, Map<String, Double>> getUserAnimeStats(
			List<String> userIdList) {
		// TODO Auto-generated method stub
		return null;
	}

}
