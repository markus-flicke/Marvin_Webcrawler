package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;
import java.util.NoSuchElementException;

public class PageReader extends FirefoxDriver{
    /**
     * The URL of ht e Marvin Search Page
     * @see <a href="https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1">Marvin Searchpage</a>
     */
    public static final String MAIN_MARVIN_URL = "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/" +
            "coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1";
    private final String eventClassName = "linkTable";
    private List<WebElement> events;
    private int pageNumber;

    public PageReader(int pageNumber) {
        super();
        this.pageNumber = pageNumber;
        this.get(MAIN_MARVIN_URL);
    }

    public void readEvents() {
        events = this.findElements(By.className(eventClassName));
    }


    public void startEmptySearch() {
        String inputSearchBarID = "genericSearchMask:search_e4ff321960e251186ac57567bec9f4ce:cm_exa_eventprocess_" +
                "basic_data:fieldset:inputField_0_1ad08e26bde39c9e4f1833e56dcce9b5:id1ad08e26bde39c9e4f1833e56dcce9b5";
        String buttonSearchID = "genericSearchMask:search";

        //Clear search bar:
        WebElement inputSearchBar = this.findElement(By.id(inputSearchBarID));
        inputSearchBar.clear();

        //Click search button:
        WebElement buttonSearch = this.findElement(By.id(buttonSearchID));
        buttonSearch.click();
    }

    public void goToPage(){
        String aPageLinkID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2idx" + pageNumber;
        boolean clicked = false;

        while(!clicked) {
            try {
                this.findElement(By.id(aPageLinkID)).click();
                clicked = true;
            } catch(ElementClickInterceptedException e) {
                this.pause(500);
            } catch(NoSuchElementException e) {
                e.printStackTrace();    //TODO: implement fastForeward/fastBackward
            }
        }
    }

    public void pause(long timeout) {
        try {
            this.wait(timeout);
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
    }


}
