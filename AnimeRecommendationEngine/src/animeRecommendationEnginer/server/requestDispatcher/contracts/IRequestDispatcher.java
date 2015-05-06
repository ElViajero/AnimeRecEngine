package animeRecommendationEnginer.server.requestDispatcher.contracts;

import java.util.Map;

import javax.ejb.Local;

@Local
public interface IRequestDispatcher {

	public void dispatchRequest(Map<String, String> request);

}
