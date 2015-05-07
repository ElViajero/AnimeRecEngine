package animeRecommendationEnginer.server.recommendationRequestHandler.helper;

import java.util.Comparator;
import java.util.Map;

/**
 * 
 * @author tejasvamsingh
 *
 */

public class ScoreComparator implements Comparator<Map<String, String>> {

	@Override
	public int compare(Map<String, String> o1, Map<String, String> o2) {

		Double scoreOne = Double.parseDouble(o1.get("score"));
		Double scoreTwo = Double.parseDouble(o2.get("score"));
		return scoreOne.compareTo(scoreTwo);

	}

}
