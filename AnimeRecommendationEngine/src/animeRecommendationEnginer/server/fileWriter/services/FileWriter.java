package animeRecommendationEnginer.server.fileWriter.services;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import animeRecommendationEnginer.server.fileWriter.contracts.IFileWriter;

@Stateless
public class FileWriter implements IFileWriter {

	@Override
	public boolean writePredictedScoreToFile(
			List<Map<String, String>> contentMapList) {

		System.out.println("Writing predicted score to file.");
		System.out.flush();

		try {
			PrintWriter outputWriter = new PrintWriter(
					"ClampExponentiatedWeight8OutputFile");
			for (Map<String, String> map : contentMapList) {
				String animeTitle = map.get("animeTitle");
				String score = map.get("score");
				String weight = map.get("weight");
				String myScore = map.get("myAnimeRating");
				String outputString = animeTitle + "\t" + score + "\t" + weight
						+ "\t" + myScore;
				outputWriter.println(outputString);
			}
			outputWriter.close();
		} catch (FileNotFoundException e) {
			System.out.println("Shit is not found.");
			System.out.flush();
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
