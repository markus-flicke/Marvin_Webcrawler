package crawler;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * @author Jakob Eckstein
 * @version 1.0
 * The Main Class of this Porject
 */
public class MarvinCrawler {
    /**
     * The relative Path to the Geckodriver for Firefox automation
     * @see <a href="https://github.com/mozilla/geckodriver">https://github.com/mozilla/geckodriver</a>
     */
    public static final String GECKODRIVER_PATH = "./lib/firefoxdriver/geckodriver";
    //public static final int ENTRIES_PER_PAGE = 300;

    public static void main(String[] args) {
        //initialise Firefox Automation:
        System.setProperty("webdriver.gecko.driver", GECKODRIVER_PATH);
        PageReader reader = new PageReader(6);
        reader.startEmptySearch();
        //reader.pause(2000);
        reader.goToPage();

    }
}
