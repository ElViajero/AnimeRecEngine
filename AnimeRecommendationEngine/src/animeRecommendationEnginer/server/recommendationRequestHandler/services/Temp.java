//package animeRecommendationEnginer.server.recommendationRequestHandler.services;
//

/**
 * 
 * GET RATINGS FOR SHOWS THAT I HAVE NOT WATCHED 
 * BUT OTHER USERS WHO SHARE AT LEAST 5 ANIME WITH ME HAVE SEEN.
 * 
 */
// public class Temp {
//	public List<Map<String, String>> getWeightedPrediction (String userId)
//	{

// GET RATINGS FOR SHOWS THAT I HAVE NOT WATCHED 
// BUT OTHER USERS WHO SHARE AT LEAST 5 ANIME WITH ME HAVE SEEN.

//		List<Map<String, String>> recList = getSimilarities(userId); 

//		List<String> userIds = new ArrayList<String>();
// getting statistics for average and std deviation.. 
//		for(Map<String,String> rec: recList)
//		{
//			if(!(userIds.contains(rec.get("userId"))))
//			{
//				userIds.add(rec.get("userId"));
//			}
//		}

// THIS IS MY MEAN AND THE STD DEVIATION.
//		userIds.add(userId);

// DB QUERY GETSTATS -> GET ALL SHOWS FOR EACH USER IN THE userIds list.
// FOR EVERY USER GET ALL SHOWS THAT THEY'VE RATED.

// GIVE ME MEAN AND STD DEV.

//		Map<String,Map<String, Double>> weightInfo = getStats(userIds);

//		Map<String,Map<String,Double>> predictedScoreInfo = new HashMap<String,Map<String,Double>>();
//		double userMean = weightInfo.get(userId).get("mean");
//		double userDev = weightInfo.get(userId).get("stdDev");

//		for(Map<String,String> rec: recList)
//		{
//			if(!predictedScoreInfo.containsKey(rec.get("animeId")))
//			{
//				Map<String,Double> temp = new HashMap<String,Double>();
//				temp.put("weight", 0.0);
//				temp.put("scoreSum", 0.0);
//		

// ADD ALL OTHER RETURN INFORMATION INCLUDING LINKS,TITLES etc. 

//				predictedScoreInfo.put(rec.get("animeId"),temp);
//			}

// 	GET THE INFORMATION OF THE USER RECOMMENDING THE ANIME

//			double mean = weightInfo.get(rec.get("userId")).get("mean");
//			double stdDev = weightInfo.get(rec.get("userId")).get("stdDev");

//			double tempSim = new Double(rec.get("similarity")) - 0.5;
//			double distance = (mean - new Double(rec.get("animeRating")))/stdDev * Math.signum(tempSim);

// CALCULAT THE NEW SCORE

//			double newScore = userMean - distance * userDev;
//      double newScore = Math.min(10, Math.max(1, newScore);
//			double insertScore = newScore * Math.abs(tempSim) + predictedScoreInfo.get(rec.get("animeId")).get("scoreSum");

// PUT THE NEW SCORES AND WEIGHT IN.

//			predictedScoreInfo.get(rec.get("animeId")).put("scoreSum", insertScore);
//			double insertWeight = Math.abs(tempSim) + predictedScoreInfo.get(rec.get("animeId")).get("weight");
//			predictedScoreInfo.get(rec.get("animeId")).put("weight", insertWeight);

// SORT BY THIS KEY
//			score = scoreSum/weight
//		}

//SORT BASED ON SCORE.

// add the animeId to this map
// and then put it into a list.

//		List<Map<String, String>> retVal = ourSort(predictedScoreInfo/*, predictedScoreInfo.keySet()*/); 
//		return retVal;

//	} 
//
// }
