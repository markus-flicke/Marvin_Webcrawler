package crawler;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;

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
    public static final int ENTRIES_PER_PAGE = 5;

    public static void main(String[] args) {
        //initialise Firefox Automation:
        System.setProperty("webdriver.gecko.driver", GECKODRIVER_PATH);
        PageReader reader = new PageReader(10);
        reader.startEmptySearch();
        reader.setEntriesPerPage(ENTRIES_PER_PAGE);
        System.out.println(reader.getMaxPage());
        reader.goToPage();
        System.out.println(reader.getCurrentPage());
        List<WebElement> events = reader.readEvents();
        //Thread[] eventsReaders = new Thread[events.size()];
        for(int i = 0; i < 2; i++) {
            new Thread(new EventReader(events.get(i))).start();
        }
        //reader.printEvents();

        reader.close();
    }
}
