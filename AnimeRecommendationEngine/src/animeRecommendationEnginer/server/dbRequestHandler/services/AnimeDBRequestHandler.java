package animeRecommendationEnginer.server.dbRequestHandler.services;

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

	@Override
	public List<Map<String, String>> getWatchedAnime(
			Map<String, String> requestMap) {
		// TODO Auto-generated method stub
		return null;
	}

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
			if (!animeEntry.containsKey("animeId")) {
				if (animeEntry.containsKey("animeLink")) {
					String[] parts = animeEntry.get("animeLink").split("/");
					animeId = parts[2];
					System.out.println("The id extracted is : " + animeId);
					animeEntry.put("animeId", animeId); // add it to the map for
														// later use.
				}
			}

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
			if (animeId.equals("") || animeTitle.equals("")
					|| animeLink.equals("")) {
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
}
