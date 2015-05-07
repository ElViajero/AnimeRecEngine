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

public class AnimeListHTMLParser implements IHTMLParser {

	@Override
	public List<Map<String, String>> parseHtml(String htmlSource) {
		List<Map<String, String>> watchedAnimeMapList = new ArrayList<Map<String, String>>();

		Document d = Jsoup.parse(htmlSource);
		Elements tableRows = d.getElementsByTag("tr");

		for (Element e : tableRows) {
			Elements animeListElements = e.getElementsByClass("animeTitle");

			if (animeListElements == null || animeListElements.size() == 0)
				continue;

			Element animeLinkElement = animeListElements.get(0);
			Element animeLinktableDataElement = animeLinkElement.parent();
			Element ratingElement = animeLinktableDataElement
					.nextElementSibling();
			Element typeElement = ratingElement.nextElementSibling();

			Map<String, String> watchedAnimeMap = new HashMap<String, String>();

			String animeLink = animeLinkElement.attr("href");
			String animeTitle = animeLinkElement.text();
			String animeRating = ratingElement.text();
			String animeType = typeElement.text();

			watchedAnimeMap.put("animeTitle", animeTitle);
			watchedAnimeMap.put("animeLink", animeLink);
			watchedAnimeMap.put("animeRating", animeRating);
			watchedAnimeMap.put("animeType", animeType);

			watchedAnimeMapList.add(watchedAnimeMap);

		}

		return watchedAnimeMapList;
	}
}
