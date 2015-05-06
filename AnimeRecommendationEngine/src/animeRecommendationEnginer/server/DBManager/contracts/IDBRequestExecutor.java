package animeRecommendationEnginer.server.DBManager.contracts;

import java.sql.ResultSet;

import javax.ejb.Local;

/**
 * 
 * This interface defines the contract for persistence operations.
 * 
 * @author tejasvamsingh
 *
 */
@Local
public interface IDBRequestExecutor {

	ResultSet executeQuery(String query);

	ResultSet exeucteUpdate(String query);

}
