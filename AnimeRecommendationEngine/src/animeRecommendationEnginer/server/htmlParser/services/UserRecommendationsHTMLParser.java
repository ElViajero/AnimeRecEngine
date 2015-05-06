package animeRecommendationEnginer.server.htmlParser.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public List<Map<String, String>> parseHtml(List<String> htmlSourceList) {

		String key = "";
		List<Map<String, String>> tableMapList = new ArrayList<Map<String, String>>();
		Map<String, String> tableRowMap = null;

		for (String line : htmlSourceList) {

			// match against an anime title
			String animeTitlePatternString = "<a .*href=\"/anime/.*>([A-Za-z :,0-9!]*?)</strong";
			Pattern animeTitlePattern = Pattern
					.compile(animeTitlePatternString);
			Matcher animeTitleMatcher = animeTitlePattern.matcher(line);

			if (animeTitleMatcher.find()) { // if we find one create a new entry
				String animeTitle = animeTitleMatcher.group(1);
				System.out.println(animeTitle);
				if (tableRowMap != null) {
					if (!tableRowMap.containsKey("numberOfRecommendations"))
						tableRowMap.put("numberOfRecommendations", "1");
					tableMapList.add(tableRowMap);
				}
				tableRowMap = new HashMap<String, String>();
				tableRowMap.put("animeTitle", animeTitle);

				// also extract out the link
				String animeLinkPatternString = "<a .*href=\"(/anime/[A-Za-z_0-9/]+/*?).*>";
				Pattern animeLinkPattern = Pattern
						.compile(animeLinkPatternString);
				Matcher animeLinkMatcher = animeLinkPattern.matcher(line);
				if (animeLinkMatcher.find()) {
					tableRowMap.put("animeLink", animeLinkMatcher.group(1));
				}
			}

			String recommenderPatternString = "<a .*href=\"/profile/.*>([A-Za-z]*?)</a>";
			Pattern recommenderPattern = Pattern
					.compile(recommenderPatternString);
			Matcher recommenderMatcher = recommenderPattern.matcher(line);
			if (recommenderMatcher.find()) {
				if (!tableRowMap.containsKey("recommendedBy"))
					tableRowMap.put("recommendedBy",
							recommenderMatcher.group(1));
			}

			// finally get how many people are recommeding it.
			String numberOfRecommendationsPatternString = ">([0-9]+?)</";
			Pattern numberOfRecommendationsPattern = Pattern
					.compile(numberOfRecommendationsPatternString);
			Matcher numberOfRecommendationsMatcher = numberOfRecommendationsPattern
					.matcher(line);
			if (numberOfRecommendationsMatcher.find()) {
				if (tableRowMap != null
						&& !tableRowMap.containsKey("numberOfRecommendations"))
					tableRowMap.put("numberOfRecommendations",
							numberOfRecommendationsMatcher.group(1));
			}

		}

		return tableMapList;
	}
}
