package animeRecommendationEnginer.server.DBManager.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.Stateless;

import animeRecommendationEnginer.server.DBManager.contracts.IDBRequestExecutor;

/**
 * 
 * This method handles the exeuction of persistence requests.
 * 
 * @author tejasvamsingh
 *
 */
@Stateless
public class DBRequestExecutor implements IDBRequestExecutor {

	private static Connection connectionInstance;
	private static Statement statementInstance;
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/AnimeRecEngine";

	public static Statement getDBManagerInstance() {

		if (statementInstance == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block

				
				e1.printStackTrace();
			}
			try {
				connectionInstance = DriverManager.getConnection(DB_URL,
						"root", "");
				statementInstance = connectionInstance.createStatement();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		return statementInstance;
	}

	@Override
	public ResultSet executeQuery(String query) {
		getDBManagerInstance();

		if (statementInstance == null) {
			
		}

		ResultSet resultSet = null;
		try {
			resultSet = statementInstance.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();

		}
		
		return resultSet;
	}

	@Override
	public boolean exeucteUpdate(String query) {

		getDBManagerInstance();

		if (statementInstance == null) {
			
		}

		ResultSet resultSet = null;
		try {
			statementInstance.executeUpdate(query);
		} catch (SQLException e) {
			
			e.printStackTrace();
			return false;

		}

		return true;
	}
}
