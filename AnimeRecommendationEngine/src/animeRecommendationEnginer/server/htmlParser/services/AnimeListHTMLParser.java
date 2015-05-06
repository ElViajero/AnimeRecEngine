package animeRecommendationEnginer.server.htmlParser.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import animeRecommendationEnginer.server.htmlParser.contracts.IHTMLParser;

public class AnimeListHTMLParser implements IHTMLParser {

	@Override
	public List<Map<String, String>> parseHtml(List<String> htmlSourceList) {

		String key = "";
		List<Map<String, String>> tableMapList = new ArrayList<Map<String, String>>();
		Map<String, String> tableRowMap = null;

		for (String line : htmlSourceList) {
			if (line.contains("</tr>") && !tableRowMap.isEmpty())
				tableMapList.add(tableRowMap);
			if (line.contains("<tr"))
				tableRowMap = new HashMap<String, String>();

			if (tableRowMap != null) { // currently processing a table
				String pattern = ">([A-Za-z :,0-9]*?)</";
				Pattern p = Pattern.compile(pattern);
				Matcher matcher = p.matcher(line);
				if (matcher.find()) {
					String word = matcher.group(1);

					if (!key.equals(""))
						tableRowMap.put(key, word);

					// extra processing step to parse out the links.
					if (key.equals("animeTitle")) {
						String linkPatternString = "<a .*href=\"(.*?)\".*";
						Pattern linkPattern = Pattern
								.compile(linkPatternString);
						Matcher linkMatcher = linkPattern.matcher(line);

						if (linkMatcher.find()) {
							String animeLink = linkMatcher.group(1);
							tableRowMap.put("animeLink", animeLink);
						}

					}

					if (word.equalsIgnoreCase("more"))
						key = "animeTitle";
					else if (key.equalsIgnoreCase("animeTitle"))
						key = "animeRating";
					else if (key.equalsIgnoreCase("animeRating"))
						key = "animeType";
					else if (key.equalsIgnoreCase("animeType"))
						key = "watchedTill";
					else
						key = "";

				}

			}

		}

		return tableMapList;
	}
}
