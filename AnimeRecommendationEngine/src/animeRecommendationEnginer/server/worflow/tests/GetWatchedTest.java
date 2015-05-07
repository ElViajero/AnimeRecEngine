package animeRecommendationEnginer.server.worflow.tests;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class GetWatchedTest {

	// @Test
	// public void getWatchedReachabilityTest() throws ClientProtocolException,
	// IOException {
	// HttpClient client = new DefaultHttpClient();
	// HttpPost post = new HttpPost(
	// "http://localhost:8080/AnimeRecommendationEngine/RequestHandler");
	// post.setEntity(new StringEntity(
	// "{\"requestId\" : \"Anime\", \"requestType\":\"getWatched\", \"userId\":\"ElViajero\"}"));
	// // "http://myanimelist.net/animelist/ElViajero");
	// HttpResponse response = client.execute(post);
	// InputStream stream = response.getEntity().getContent();
	// BufferedReader x = new BufferedReader(new InputStreamReader(stream));
	// String line = x.readLine();
	// while (line != null) {
	// System.out.println(line);
	// line = x.readLine();
	// }
	// }

	@Test
	public void getWatchedFetchTest() throws ClientProtocolException,
			IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://localhost:8080/AnimeRecommendationEngine/RequestHandler");
		post.setEntity(new StringEntity(
				"{\"requestId\" : \"Anime\", \"requestType\":\"getWatched\", \"userId\":\"pafk\"}"));
		// "http://myanimelist.net/animelist/ElViajero");
		HttpResponse response = client.execute(post);
		/*
		 * InputStream stream = response.getEntity().getContent();
		 * BufferedReader x = new BufferedReader(new InputStreamReader(stream));
		 * String line = x.readLine(); while (line != null) {
		 * System.out.println(line); line = x.readLine(); }
		 */
	}
}
