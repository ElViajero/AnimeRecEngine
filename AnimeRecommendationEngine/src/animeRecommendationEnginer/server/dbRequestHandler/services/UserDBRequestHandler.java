package animeRecommendationEnginer.server.dbRequestHandler.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

	/**
	 * finds the mean and the standard deviation of the animes watched by the
	 * user.
	 * 
	 * @author sarahwatanabe
	 * 
	 */
	@Override
	public Map<String, Map<String, Double>> getUserAnimeStats(
			List<String> userIdList) {
		// parameter is a list of userIdList

		Map<String, Map<String, Double>> returnMap = new HashMap<String, Map<String, Double>>();
		ArrayList<Integer> ratingsArr = new ArrayList<Integer>();

		for (int i = 0; i < userIdList.size(); i--) {

			// formulate the query
			String query = "SELECT * FROM WatchedAnimeTable WHERE userId="
					+ "\"" + userIdList.get(i) + "\"" + ";";
			// execute query
			ResultSet queryResult = iDBRequestExecutor.executeQuery(query);

			// status is failed currently
			returnMap.put("Status", new HashMap<String, Double>());
			returnMap.get("Status").put("Failed", 0.0);

			// variables
			double mean = 0;
			double sum = 0;
			int count = 0;
			double std_dev_sum = 0;

			try {
				// get statistic of all anime titles
				while (queryResult.next()) {

					// status map is now successful
					returnMap.get("Status").put("Success", 0.0);

					String animeRating = queryResult.getString("animeRating");
					int rating = Integer.parseInt(animeRating);
					sum += rating;
					ratingsArr.add(rating);
					count++;
				}
				// find the mean
				mean = (double) sum / (double) count;
				for (int j = 0; j < count; j++) {
					double diff = ratingsArr.get(j) - mean;
					diff = diff * diff;
					std_dev_sum += diff;
				}
				double std_dev = std_dev_sum / (double) count;
				// adding to hashmap
				returnMap.put(userIdList.get(i), new HashMap<String, Double>());
				returnMap.get(userIdList.get(i)).put("mean", mean);
				returnMap.get(userIdList.get(i)).put("std dev", std_dev);
				ratingsArr.clear();

			} catch (SQLException e) {
				return returnMap;
			}
		}
		System.out.println("Fetched from DB : " + returnMap);
		return returnMap;
	}

	/**
	 * 
	 * 
	 * This class adds the user similarity score between two users into the
	 * database.
	 * 
	 * @author tejasvamsingh
	 * @param userIdOne
	 * @param userIdTwo
	 * @param score
	 * @return
	 */
	public boolean updateUserSimilarity(String userIdOne, String userIdTwo,
			double score) {

		// formulate the query
		String query = "INSERT INTO UserSimilarityTable VALUES(\""
				+ userIdOne
				+ "\",\""
				+ userIdTwo
				+ "\",\""
				+ score
				+ "\")"
				+ " ON DUPLICATE KEY UPDATE similarityScore=VALUES(similarityScore);";
		if (!iDBRequestExecutor.exeucteUpdate(query)) {
			System.out
					.println("Something went wrong when performing similarity update.");
			return false;
		}

		return true;
	}
}
