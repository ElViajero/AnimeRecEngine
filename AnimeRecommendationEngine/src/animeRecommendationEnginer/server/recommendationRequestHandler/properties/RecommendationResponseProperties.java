package animeRecommendationEnginer.server.recommendationRequestHandler.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class contains all the fields required by client.
 * 
 * @author tejasvamsingh
 *
 */
public class RecommendationResponseProperties {

	String success;
	String errorMessage;
	List<Map<String, String>> contentList;

	public RecommendationResponseProperties() {
		success = "false";
		errorMessage = "";
		contentList = new ArrayList<Map<String, String>>();
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<Map<String, String>> getContentList() {
		return contentList;
	}

	public void setContentList(List<Map<String, String>> contentList) {
		this.contentList = contentList;
	}

}
