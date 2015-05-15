package animeRecommendationEnginer.server.dbRequestHandler.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
			Set<String> userIdList) {
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

	@Override
	/* This method gets the rated anime from similar users.
	 * 
	 */
	public List<Map<String, String>> getRatedAnimeFromSimilarUsers(
			String userId) {
		
		String query = "SELECT a.animeId as animeId, a.animeTitle as animeTitle, a.animeRating as animeRating, d.similarityScore as similarityScore FROM "
				+ "WatchedAnimeTable as a "
				+ "INNER JOIN " + 
				"(SELECT c.userId as userId FROM WatchedAnimeTable as b INNER JOIN WatchedAnimeTable as c "
				+ "ON(b.UserId LIKE \""+ userId +"\" AND b.AnimeId LIKE c.AnimeId AND b.AnimeRating NOT LIKE \"-\" AND c.AnimeRating NOT LIKE \"-\") "
				+ "GROUP BY c.userId HAVING COUNT(c.animeId) > 5 ) as e "
				+ "ON (a.userId LIKE e.userId) " 
				+ "INNER JOIN usersimilaritytable as d " 
				+ "ON (d.userOneId LIKE "+ userId +" AND d.userTwoId LIKE a.userId) " 
				+ "WHERE a.animeId NOT IN " 
				+ "(SELECT animeId FROM WatchedAnimeTable WHERE userId =\""+ userId + "\") "
				+ "and a.animeRating NOT LIKE \"-\"";
		
		// execute query
		ResultSet queryResult = iDBRequestExecutor.executeQuery(query);
		
		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>() ; 
		
		// status is failed currently
		Map<String,String> status = new HashMap<String, String>();
		status.put("Status", "Failed");
		returnList.add(status);

		try {
				// get statistic of all anime titles
				while (queryResult.next()) {

					// status map is now successful
					status.put("Status", "Success");
					
					
					//Making a anime entry map
					Map<String, String> animeEntryMap = new HashMap<String, String>();
					String animeId = queryResult.getString("animeId");
					String animeTitle = queryResult.getString("animeTitle");
					String animeRating = queryResult.getString("animeRating");
					String similarityScore = queryResult.getString("similarityScore");
					animeEntryMap.put("animeId", animeId);
					animeEntryMap.put("animeTitle", animeTitle);
					animeEntryMap.put("animeRating", animeRating);
					animeEntryMap.put("similarityScore", similarityScore);
					returnList.add(animeEntryMap);
				}

			} catch (SQLException e) {
				return returnList;
			}
			System.out.println("Fetched from DB : " + returnList);
			return returnList;
		}
}
