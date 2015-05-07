package animeRecommendationEnginer.server.RequestHandler.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import animeRecommendationEnginer.server.recommendationRequestHandler.properties.RecommendationResponseProperties;
import animeRecommendationEnginer.server.requestDispatcher.contracts.IRequestDispatcher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * This servlet serves as the entry point into the anime recommendation service.
 * 
 * @author tejasvamsingh Servlet implementation class RequestHandler
 */
@WebServlet("/RequestHandler")
public class RequestHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	IRequestDispatcher iRequestDispatcher;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RequestHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		java.io.Writer w = response.getWriter();
		w.append("<html>");
		w.append("<body>");
		w.append("<h1>This is the anime recommendation service.</h1>");
		w.append("</body>");
		w.append("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get the JSON data.
		BufferedReader reader = request.getReader();
		String line = reader.readLine();
		String jsonString = "";
		while (line != null) {
			jsonString += line;
			line = reader.readLine();
		}
		// parse it into a map.
		Type stringStringMap = new TypeToken<Map<String, String>>() {
		}.getType();
		Gson gson = new Gson();
		Map<String, String> requestMap = gson.fromJson(jsonString,
				stringStringMap);

		// dispatch the request.
		RecommendationResponseProperties result = iRequestDispatcher
				.dispatchRequest(requestMap);

		// return the response

		// Writer w = response.getWriter();
		// w.append(gson.toJson(result));
		// w.close();
	}
}
