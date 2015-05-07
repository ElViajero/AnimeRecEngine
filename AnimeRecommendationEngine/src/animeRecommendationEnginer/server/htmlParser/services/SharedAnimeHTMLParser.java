//package animeRecommendationEnginer.server.htmlParser.services;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import animeRecommendationEnginer.server.htmlParser.contracts.IHTMLParser;
//
///**
// * This class parses out shared anime ratings between users.
// * 
// * @author tejasvamsingh
// *
// */
//public class SharedAnimeHTMLParser implements IHTMLParser {
//
//	@Override
//	public List<Map<String, String>> parseHtml(List<String> htmlSourceList) {
//
//		List<Map<String, String>> tableMapList = new ArrayList<Map<String, String>>();
//		Map<String, String> tableRowMap = null;
//		String key = "";
//		for (String line : htmlSourceList) {
//			if (line.contains("</table"))
//				break;
//			if (line.contains("</tr>") && !tableRowMap.isEmpty()) {
//				tableMapList.add(tableRowMap);
//			}
//			if (line.contains("<tr"))
//				tableRowMap = new HashMap<String, String>();
//
//			if (tableRowMap != null) { // currently processing a table
//				String pattern = ">([A-Za-z :,0-9]*?)</";
//				Pattern p = Pattern.compile(pattern);
//				Matcher matcher = p.matcher(line);
//
//				if (matcher.find()) {
//					String word = matcher.group(1);
//					if (word.equals(""))
//						continue;
//					if (!key.equals(""))
//						tableRowMap.put(key, word);
//					if (word.equalsIgnoreCase("title"))
//						key = "animeTitle";
//					else if (key.equalsIgnoreCase("animeTitle"))
//						key = "userRating";
//					else if (key.equalsIgnoreCase("userRating"))
//						key = "myRating";
//					else if (key.equalsIgnoreCase("myRating"))
//						key = "ratingDifference";
//					else if (key.equalsIgnoreCase("ratingDifference"))
//						key = "animeTitle";
//					else
//						key = "";
//				}
//			}
//		}
//
//		System.out.println(tableMapList);
//		return tableMapList;
//	}
// }