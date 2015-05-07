package animeRecommendationEnginer.server.htmlParser.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import animeRecommendationEnginer.server.htmlParser.contracts.IHTMLParser;

/**
 * 
 * This class parses the usernames and recommendations of various users based on
 * a given anime.
 * 
 * @author tejasvamsingh
 *
 */
public class UserRecommendationsHTMLParser implements IHTMLParser {

	@Override
	public List<Map<String, String>> parseHtml(String htmlSource) {

		List<Map<String, String>> recommendedAnimeMapList = new ArrayList<Map<String, String>>();
		Document d = Jsoup.parse(htmlSource);
		// get the table data elements
		Elements tableDataElements = d.getElementsByTag("td");

		for (Element tableDataElement : tableDataElements) {

			String animeTitle = ""; // initialize our fields
			String animeLink = ""; // to empty
			String recommenderId = ""; // for each new table data
			String numberOfRecommendations = "";

			Elements divElements = tableDataElement.getElementsByTag("div");
			for (Element divElement : divElements) {
				boolean noUpdate = false;
				// try and see if the div contains links
				Elements animeLinkElements = divElement
						.getElementsByAttribute("href");
				if (animeLinkElements == null || animeLinkElements.size() == 0)
					continue;

				Element animeLinkElement = animeLinkElements.first();
				String currentLink = animeLinkElement.attr("href");
				String[] parts = currentLink.split("/");
				// see if the link is an anime link
				if (parts.length != 4 || !parts[1].equals("anime"))
					continue;

				// check if it's the first anime link. There
				// may be multiple inside a table data.
				if (!animeTitle.equals(""))
					continue;
				if (!animeLink.equals(""))
					continue;

				// if so set the fields
				animeLink = currentLink;
				animeTitle = animeLinkElement.text();

				// get the immediate next div
				Element nextDivElement = divElement.nextElementSibling();
				if (nextDivElement == null)
					continue;
				Elements nextDivLinkElements = nextDivElement
						.getElementsByAttribute("href");
				if (nextDivLinkElements == null)
					continue;
				// check links in div for a profile link
				for (Element linkElement : nextDivLinkElements) {
					if (!linkElement.attr("href").contains("profile"))
						continue;
					// grab it if it's the first one
					if (recommenderId.equals("")) {
						recommenderId = linkElement.text();
					}
					break;

				}

				// finally check the number of recommendations.
				try {
					numberOfRecommendations = nextDivElement
							.nextElementSibling().getElementsByTag("strong")
							.first().text();

				} catch (NullPointerException e) {
					continue;
				}

			}

			// check for complete entries and add to List.
			if (!animeTitle.equalsIgnoreCase("")) {
				Map<String, String> recommededAnimeMap = new HashMap<String, String>();
				recommededAnimeMap.put("animeTitle", animeTitle);
				recommededAnimeMap.put("animeLink", animeLink);
				recommededAnimeMap.put("recommenderId", recommenderId);

				// special adjustment for numRecommendations
				// to account for TOTAL recommendations.
				if (numberOfRecommendations.equals(""))
					numberOfRecommendations = "1";
				else
					numberOfRecommendations = String.valueOf(Integer
							.parseInt(numberOfRecommendations) + 1);
				recommededAnimeMap.put("numberOfRecommendations",
						numberOfRecommendations);

				// add the map to the list.
				recommendedAnimeMapList.add(recommededAnimeMap);
			}

		}

		return recommendedAnimeMapList;
	}
}
