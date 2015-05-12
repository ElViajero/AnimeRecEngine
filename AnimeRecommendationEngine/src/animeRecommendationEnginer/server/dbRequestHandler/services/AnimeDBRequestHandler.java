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
import animeRecommendationEnginer.server.dbRequestHandler.contracts.IAnimeDBRequestHandler;

/**
 * This class handles anime persistence operations.
 * 
 * @author tejasvamsingh
 *
 */
@Stateless
public class AnimeDBRequestHandler implements IAnimeDBRequestHandler {

	@Inject
	IDBRequestExecutor iDBRequestExecutor;

	/**
	 * @author tejasvamsingh
	 */
	@Override
	public List<Map<String, String>> getWatchedAnime(String userId) {

		// formulate the query
		String query = "SELECT * FROM WatchedAnimeTable WHERE userId LIKE "
				+ "\"" + userId + "\"" + ";";

		ResultSet queryResult = iDBRequestExecutor.executeQuery(query);

		// get the results
		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put("Status", "Failed");
		statusMap.put("Reason", "UserId not found.");
		List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>();
		resultMapList.add(statusMap);
		try {

			while (queryResult.next()) {

				statusMap.put("Status", "Success");
				String animeId = queryResult.getString("animeId");
				String animeTitle = queryResult.getString("animeTitle");
				String animeRating = queryResult.getString("animeRating");
				Map<String, String> animeEntryMap = new HashMap<String, String>();
				animeEntryMap.put("animeId", animeId);
				animeEntryMap.put("animeTitle", animeTitle);
				animeEntryMap.put("animeRating", animeRating);
				animeEntryMap.put("userId", userId);
				resultMapList.add(animeEntryMap);
			}
		} catch (SQLException e) {
			return resultMapList;
		}
		System.out.println("Fetched from DB : " + resultMapList);
		return resultMapList;
	}

	/**
	 * @author tejasvamsingh
	 */
	@Override
	public boolean updateWatchedAnime(List<Map<String, String>> requestMapList,
			String userId) {

		boolean successStatus = false;
		for (Map<String, String> animeEntry : requestMapList) {
			System.out.println("animeEntry in updateWatchedAnime is : "
					+ animeEntry);

			// retrieve the parameters
			String animeId = "";
			String animeTitle = "";
			animeEntry.get("animeTitle");
			String animeRating = "";
			animeEntry.get("animeRating");

			if (animeEntry.containsKey("animeTitle"))
				animeTitle = animeEntry.get("animeTitle");
			if (animeEntry.containsKey("animeRating"))
				animeRating = animeEntry.get("animeRating");
			if (animeEntry.containsKey("animeId"))
				animeId = animeEntry.get("animeId");

			// try and extract the animeId
			animeId = extractAnimeId(animeEntry);

			// check if some fields are still missing
			if (animeId.equals("") || animeTitle.equals("")
					|| animeRating.equals("")) {
				System.out
						.println("animeId, animeTitle or AnimRating are blank in WatchedAnimeTable."
								+ "The animeTitle is  : "
								+ animeTitle
								+ " The animeId is : "
								+ animeId
								+ " The animeRating is : " + animeRating);
				System.out.flush();
				return false;
			}

			// formulate the query
			String query = "INSERT INTO WatchedAnimeTable VALUES (\"" + userId
					+ "\",\"" + animeId + "\",\"" + animeTitle + "" + "\",\""
					+ animeRating + "\") ON DUPLICATE KEY UPDATE "
					+ "animeTitle=VALUES(animeTitle),"
					+ "animeRating=VALUES(animeRating);";

			// execute the query
			if (!iDBRequestExecutor.exeucteUpdate(query)) {
				System.out
						.println("something went wrong updating WatchedAnimeTable."
								+ "The query is  :" + query);
				return false;
			}

		}
		// also update the anime table
		if (!updateAnime(requestMapList))
			return false;

		return true;

	}

	/**
	 * @author tejasvamsingh
	 * @param animeEntry
	 * @return
	 */
	private String extractAnimeId(Map<String, String> animeEntry) {
		String animeId = "";
		if (!animeEntry.containsKey("animeId")) {
			if (animeEntry.containsKey("animeLink")) {
				String[] parts = animeEntry.get("animeLink").split("/");
				animeId = parts[2];
				System.out.println("The id extracted is : " + animeId);
				animeEntry.put("animeId", animeId); // add it to the map for
													// later use.
			}
		}
		return animeId;
	}

	/**
	 * 
	 * @author tejasvamsingh
	 */
	@Override
	public boolean updateAnime(List<Map<String, String>> requestMapList) {

		for (Map<String, String> animeEntry : requestMapList) {

			System.out.println("animeEntry is : " + animeEntry);

			// try and extract fields.
			String animeId = animeEntry.containsKey("animeId") ? animeEntry
					.get("animeId") : "";
			String animeTitle = animeEntry.containsKey("animeTitle") ? animeEntry
					.get("animeTitle") : "";
			String animeType = animeEntry.containsKey("animeType") ? animeEntry
					.get("animeType") : "";
			String animeLink = animeEntry.containsKey("animeLink") ? animeEntry
					.get("animeLink") : "";
			String animeImage = animeEntry.containsKey("animeImage") ? animeEntry
					.get("animeImage") : "";
			String animeRank = animeEntry.containsKey("animeRank") ? animeEntry
					.get("animeRank") : "";

			// check mandatory fields
			if (animeId.equals("") || animeTitle.equals("")) {
				System.out
						.println("animeId or animeTitle are blank in AnimeTable."
								+ "The animeTitle is  : "
								+ animeTitle
								+ "The animeId is : " + animeId);

				return false;
			}

			// formulate the query
			String query = "INSERT INTO AnimeTable VALUES (" + "\"" + animeId
					+ "\",\"" + animeTitle + "\",\"" + animeRank + "\",\""
					+ animeLink + "\",\"" + animeImage + "\",\"" + animeType
					+ "\") ON DUPLICATE KEY UPDATE ";

			query += "animeTitle=VALUES(animeTitle)";

			if (!animeLink.equals(""))
				query += ",animeLink=VALUES(animeLink)";
			if (!animeType.equals(""))
				query += ",animeType=VALUES(animeType)";
			if (!animeImage.equals(""))
				query += ",animeImage=VALUES(animeImage)";
			if (!animeRank.equals(""))
				query += ",animeRank=VALUES(animeRank)";

			query += ";";

			// execute the update
			if (!iDBRequestExecutor.exeucteUpdate(query)) {
				System.out.println("something went wrong updating AnimeTable."
						+ "The query is  :" + query);
				return false;
			}

		}

		return true;
	}

	/**
	 * @author tejasvamsingh
	 */
	@Override
	public List<Map<String, String>> getAnime(String animeId) {

		// formulate the query
		String query = "SELECT * FROM AnimeTable WHERE animeId LIKE \""
				+ animeId + "\";";

		ResultSet queryResult = iDBRequestExecutor.executeQuery(query);

		// get the results
		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put("Status", "Failed");
		statusMap.put("Reason", "UserId not found.");
		List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>();
		resultMapList.add(statusMap);

		try {
			while (queryResult.next()) {

				statusMap.put("Status", "Success");

				// extract fields.
				animeId = queryResult.getString("animeId"); // redundant
				String animeTitle = queryResult.getString("animeTitle");
				String animeLink = queryResult.getString("animeLink");
				String animeImage = queryResult.getString("animeImage");
				String animeType = queryResult.getString("animeType");
				String animeRank = queryResult.getString("animeRank");

				Map<String, String> animeEntryMap = new HashMap<String, String>();
				animeEntryMap.put("animeTitle", animeTitle);
				animeEntryMap.put("animeLink", animeLink);
				animeEntryMap.put("animeRank", animeRank);
				animeEntryMap.put("animeType", animeType);
				animeEntryMap.put("animeImage", animeImage);
				resultMapList.add(animeEntryMap);

			}
		} catch (SQLException e) {
			System.out.println("something went wrong will fetching anime.");
			e.printStackTrace();
		}

		return resultMapList;
	}

	/**
	 * @author tejasvamsingh
	 */
	@Override
	public boolean updateAnimeRecommendations(
			List<Map<String, String>> recommendedAnimeMapList, String animeId) {

		// for each recommended anime.
		for (Map<String, String> recommendedAnimeEntry : recommendedAnimeMapList) {

			// try and extract the animeId.
			String recommendedAnimeId = extractAnimeId(recommendedAnimeEntry);
			String recommederId = recommendedAnimeEntry.get("recommenderId");
			String numberOfRecommendations = recommendedAnimeEntry
					.get("numberOfRecommendations");

			// hard fail.
			if (recommendedAnimeId.equals("")) {
				System.out.println("recommendedAnimeId is empty. AnimeEntry : "
						+ recommendedAnimeEntry);
				return false;
			}

			// formulate the query
			String query = "INSERT INTO AnimeRecTable VALUES(" + "\"" + animeId
					+ "\"," + "\"" + recommendedAnimeId + "\"," + "\""
					+ recommederId + "\"," + "\"" + numberOfRecommendations
					+ "\") ON DUPLICATE KEY UPDATE "
					+ "numRecommendations=VALUES(numRecommendations),"
					+ "recommenderId=VALUES(recommenderId);";

			if (!iDBRequestExecutor.exeucteUpdate(query)) {
				System.out.println("something is going wrong.");
				System.out.flush();
				return false;
			}

			// change recommededAnimeId to animeId
			recommendedAnimeEntry.put("animeId", recommendedAnimeId);

		}

		// try and add to anime table as well
		updateAnime(recommendedAnimeMapList);
		return true;
	}

	/**
	 * @author tejasvamsingh
	 */
	@Override
	public List<Map<String, String>> getRecommendedAnime(String animeId) {

		// formulate the query
		String query = " SELECT * FROM ( AnimeRectable JOIN animeTable ON AnimeRecTable.recommendedAnimeId=animeTable.animeId) "
				+ "WHERE AnimeRecTable.animeId LIKE \"" + animeId + "\";";

		// execute the query
		ResultSet queryResult = iDBRequestExecutor.executeQuery(query);

		// get the results
		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put("Status", "Failed");
		statusMap.put("Reason", "UserId not found.");
		List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>();
		resultMapList.add(statusMap);

		try {
			while (queryResult.next()) {

				// populate the fields.
				statusMap.put("Status", "Success");
				animeId = queryResult.getString("animeId");
				String recommendedAnimeId = queryResult
						.getString("recommendedAnimeId");
				String recommenderId = queryResult.getString("recommenderId");
				String numberOfRecommendations = queryResult
						.getString("numRecommendations");
				String animeTitle = queryResult.getString("animeTitle");

				Map<String, String> animeEntryMap = new HashMap<String, String>();
				animeEntryMap.put("animeId", animeId);
				animeEntryMap.put("recommendedAnimeId", recommendedAnimeId);
				animeEntryMap.put("numberOfRecommendations",
						numberOfRecommendations);
				animeEntryMap.put("recommenderId", recommenderId);
				animeEntryMap.put("animeTitle", animeTitle);
				resultMapList.add(animeEntryMap);
				System.out.println("found entry : " + animeEntryMap);
				System.out.flush();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultMapList;
	}

	/**
	 * @author tejasvamsingh
	 */
	@Override
	public List<Map<String, String>> getSharedAnime(String userId1,
			String userId2) {

		// formulate the query
		String query = "SELECT A.animeId AS animeId, A.animeRating AS userOneRating, B.animeRating AS userTwoRating  FROM "
				+ "(WatchedAnimeTable as A JOIN WatchedAnimeTable as B ON A.animeId=B.animeId) "
				+ "WHERE A.userId=\""
				+ userId1
				+ "\""
				+ " AND B.userId=\""
				+ userId2 + "\";";

		// execute the query
		ResultSet queryResult = iDBRequestExecutor.executeQuery(query);

		List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>();
		Map<String, String> statusMap = new HashMap<String, String>();

		statusMap.put("Status", "Failed");
		statusMap.put("Reason", "No shared anime found.");

		resultMapList.add(statusMap);

		// fetch the results
		try {
			while (queryResult.next()) {

				statusMap.put("Status", "Success");

				// populate the fields and add to a map
				Map<String, String> animeEntryMap = new HashMap<String, String>();
				animeEntryMap.put("animeId", queryResult.getString("animeId"));
				animeEntryMap.put("userOneRating",
						queryResult.getString("userOneRating"));
				animeEntryMap.put("userTwoRating",
						queryResult.getString("userTwoRating"));
				resultMapList.add(animeEntryMap);
			}
		} catch (SQLException e) {
			System.out.println("something went wrong while fetching anime.");
			e.printStackTrace();
		}
		return resultMapList;
	}
}
