package animeRecommendationEnginer.server.fileWriter.contracts;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

@Local
public interface IFileWriter {

	public boolean writePredictedScoreToFile(
			List<Map<String, String>> contentMapList);

}
