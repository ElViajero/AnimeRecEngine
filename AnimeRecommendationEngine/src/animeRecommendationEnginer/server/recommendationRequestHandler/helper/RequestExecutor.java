package animeRecommendationEnginer.server.recommendationRequestHandler.helper;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Singleton;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * This class instantiates a web driver to open the requested url and retrieves
 * the page source.
 * 
 * @author tejasvamsingh
 *
 */

@Singleton
public class RequestExecutor {

	final static WebDriver webDriver = new FirefoxDriver();

	public List<String> getHTMLSource(String requestURL) {
		webDriver.get(requestURL);
		String htmlSouceString = webDriver.getPageSource();
		String[] lines = htmlSouceString.split("\n");
		System.out.println("list is : " + Arrays.asList(lines));
		return Arrays.asList(lines);
	}

}
