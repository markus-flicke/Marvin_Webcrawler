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
     * The URL of ht e Marvin Search Page
     * @see <a href="https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1">Marvin Searchpage</a>
     */
    public static final String MAIN_MARVIN_URL = "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/" +
            "coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1";
    /**
     * The relative Path to the Geckodriver for Firefox automation
     * @see <a href="https://github.com/mozilla/geckodriver">https://github.com/mozilla/geckodriver</a>
     */
    public static final String GECKODRIVER_PATH = "./lib/firefoxdriver/geckodriver";
    //public static final int ENTRIES_PER_PAGE = 300;

    //main webdriver
    private static WebDriver driver;

    public static void main(String[] args) {
        //initialise Firefox Automation:
        System.setProperty("webdriver.gecko.driver", GECKODRIVER_PATH);
        driver = new FirefoxDriver();
        //navigate to Marvin search:
        driver.get(MAIN_MARVIN_URL);

        startEmptySearch();

    }

    private static void startEmptySearch() {
        String inputSearchBarID = "genericSearchMask:search_e4ff321960e251186ac57567bec9f4ce:cm_exa_eventprocess_" +
                "basic_data:fieldset:inputField_0_1ad08e26bde39c9e4f1833e56dcce9b5:id1ad08e26bde39c9e4f1833e56dcce9b5";
        String buttonSearchID = "genericSearchMask:search";

        //Clear search bar:
        WebElement inputSearchBar = driver.findElement(By.id(inputSearchBarID));
        inputSearchBar.clear();

        //Click search button:
        WebElement buttonSearch = driver.findElement(By.id(buttonSearchID));
        buttonSearch.click();
    }

}
