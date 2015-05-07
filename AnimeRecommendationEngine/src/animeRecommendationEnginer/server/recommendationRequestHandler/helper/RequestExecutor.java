package animeRecommendationEnginer.server.recommendationRequestHandler.helper;

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

	public String getHTMLSource(String requestURL) {
		webDriver.get(requestURL);
		String htmlSouceString = webDriver.getPageSource();
		System.out.println("HTML Source is :" + htmlSouceString);
		return htmlSouceString;
	}

}
