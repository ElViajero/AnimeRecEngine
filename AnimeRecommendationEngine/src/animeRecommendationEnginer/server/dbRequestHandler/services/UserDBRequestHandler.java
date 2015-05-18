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
import animeRecommendationEnginer.server.dbRequestHandler.contracts.IAnimeDBRequestHandler;
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

	@Inject
	IAnimeDBRequestHandler iAnimeDBRequestHandler;

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

		for (String userId : userIdList) {
			if (userId == null)
				continue;

			List<Double> ratingsArr = new ArrayList<Double>();

			// formulate the query
			String query = "SELECT * FROM WatchedAnimeTable WHERE userId="
					+ "\"" + userId + "\" AND animeRating NOT LIKE \"-\"" + ";";
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
				// get statistics of all anime titles
				while (queryResult.next()) {

					// status map is now successful
					returnMap.get("Status").put("Success", 0.0);

					String animeRating = queryResult.getString("animeRating");

					try {
						double rating = Double.parseDouble(animeRating);
						sum += rating;
						ratingsArr.add(rating);
						count++;
					} catch (NumberFormatException e) {
						continue;
					}
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
				returnMap.put(userId, new HashMap<String, Double>());
				returnMap.get(userId).put("mean", mean);
				returnMap.get(userId).put("stdDev", std_dev);

			} catch (SQLException e) {
				return returnMap;
			}
		}

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
	/*
	 * This method gets the rated anime from similar users.
	 * 
	 * GETTING THE SCORE, ANIME TITLE, RATING INFO FROM USERS WITH WHOM WE SHARE
	 * >=5 SHOWS THAT HAVE BEEN RATED.
	 */
	public List<Map<String, String>> getRatedAnimeFromSimilarUsers(String userId) {

		String query = "select userId, animeTitle, animeId, userAnimeRating, myAnimeRating, "
				+ "similarityScore from (UserSimilarityTable as S JOIN ( SELECT R.userId as userId, "
				+ "R.animeTitle as animeTitle, R.animeId AS animeId, R.userAnimeRating AS "
				+ "userAnimeRating, E.animeRating AS myAnimeRating FROM "
				+ "( (SELECT animeId,animeRating FROM WatchedAnimeTable where userId=\""
				+ userId
				+ "\" AND "
				+ "animeRating NOT LIKE  \"-\") AS E JOIN (SELECT Y.userId as userId,Y.animeId as "
				+ "animeId,Y.animeTitle as animeTitle,Y.animeRating as userAnimeRating FROM "
				+ "((SELECT b.userId FROM ((SELECT * FROM WatchedAnimeTable where userId=\""
				+ userId
				+ "\" AND animeRating NOT LIKE  \"-\")AS a"
				+ " JOIN (SELECT * FROM WatchedAnimeTable "
				+ "WHERE userId NOT LIKE \""
				+ userId
				+ "\" AND animeRating NOT LIKE \"-\") AS b ON a.animeId "
				+ " = b.animeId) GROUP BY (b.userId) HAVING COUNT(b.animeId)>5) AS X JOIN "
				+ "(SELECT * FROM WatchedAnimeTable where userId NOT LIKE \""
				+ userId
				+ "\" AND animeRating "
				+ "NOT LIKE  \"-\") AS Y ON X.userId = Y.userId)) AS R ON R.animeId=E.animeId)) AS "
				+ "Q ON S.userOneId=\""
				+ userId
				+ "\" and S.userTwoId =Q.userId) WHERE userId NOT LIKE \""
				+ userId + "\";";

		System.out.println("The query is : " + query);

		// execute query
		ResultSet queryResult = iDBRequestExecutor.executeQuery(query);

		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();

		// status is failed currently
		Map<String, String> status = new HashMap<String, String>();
		status.put("Status", "Failed");
		returnList.add(status);

		try {
			// get statistic of all anime titles
			while (queryResult.next()) {

				// status map is now successful
				status.put("Status", "Success");

				// Making a anime entry map
				Map<String, String> animeEntryMap = new HashMap<String, String>();
				userId = queryResult.getString("userId");
				String animeId = queryResult.getString("animeId");
				String animeTitle = queryResult.getString("animeTitle");
				String myAnimeRating = queryResult.getString("myAnimeRating");
				String animeRating = queryResult.getString("userAnimeRating");

				try {
					Double.parseDouble(animeRating);
				} catch (NumberFormatException e) {
					continue;
				}
				String similarityScore = queryResult
						.getString("similarityScore");

				animeEntryMap.put("userId", userId);
				animeEntryMap.put("animeId", animeId);
				animeEntryMap.put("animeTitle", animeTitle);
				animeEntryMap.put("userAnimeRating", animeRating);
				animeEntryMap.put("similarityScore", similarityScore);
				animeEntryMap.put("myAnimeRating", myAnimeRating);
				returnList.add(animeEntryMap);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return returnList;
		}
		System.out.println("Fetched from DB in getAnimeFromSimilarUsers : "
				+ returnList);
		System.out.flush();
		return returnList;
	}

	@Override
	public List<String> getSimilarUsers(String userId) {
		String query = "SELECT c.userId as userId FROM WatchedAnimeTable as b INNER JOIN WatchedAnimeTable as c "
				+ "ON(b.UserId LIKE \""
				+ userId
				+ "\" AND b.AnimeId LIKE c.AnimeId AND b.AnimeRating NOT LIKE \"-\" AND c.AnimeRating NOT LIKE \"-\") "
				+ "GROUP BY c.userId HAVING COUNT(c.animeId) > 5 ; ";

		List<String> returnUserIdList = new ArrayList<String>();
		// execute query
		ResultSet queryResult = iDBRequestExecutor.executeQuery(query);

		try {
			while (queryResult.next()) {
				userId = queryResult.getString("userId");
				returnUserIdList.add(userId);
			}
		} catch (SQLException e) {
			return returnUserIdList;
		}

		System.out.flush();

		return returnUserIdList;
	}
}
