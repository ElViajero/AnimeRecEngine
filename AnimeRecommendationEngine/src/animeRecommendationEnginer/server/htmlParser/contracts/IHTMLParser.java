package animeRecommendationEnginer.server.htmlParser.contracts;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

@Local
public interface IHTMLParser {
	List<Map<String, String>> parseHtml(List<String> htmlSourceList);
}
