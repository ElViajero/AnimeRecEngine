package animeRecommendationEnginer.server.worflow.tests;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class DBQueryExecutorTest {

	@Test
	public void executeQueryTest() throws ClientProtocolException, IOException {

		HttpClient client = new DefaultHttpClient();
		// HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://localhost:8080/AnimeRecommendationEngine/RequestHandler");
		// "http://myanimelist.net/animelist/ElViajero");
		HttpResponse response = client.execute(post);

	}
}
