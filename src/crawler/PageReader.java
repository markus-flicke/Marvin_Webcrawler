package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class PageReader extends HtmlUnitDriver{
    /**
     * The URL of ht e Marvin Search Page
     * @see <a href="https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1">Marvin Searchpage</a>
     */
    public static final String MAIN_MARVIN_URL = "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/" +
            "coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1";
    private final String eventClassName = "linkTable";
    private List<WebElement> events;
    private int pageNumber;
    private static int shortWait = 100;
    private static int longWait = 500; //TODO: Check where to use long wait and where to use short wait

    public PageReader(int pageNumber) {
        super(true);
        this.pageNumber = pageNumber;
        this.get(MAIN_MARVIN_URL);
    }

    public List<WebElement> readEvents() {
        events = this.findElements(By.className(eventClassName));
        return events;
    }

    public void printEvents() {
        Iterator<WebElement> it = events.iterator();
        while(it.hasNext()){
            System.out.println(it.next().getText());
        }
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
                while(this.getCurrentPage() != pageNumber){
                    this.pause(shortWait);
                }
                clicked = true;
            } catch(ElementClickInterceptedException e) {
                this.pause(longWait);
            } catch(NoSuchElementException e) {
                e.printStackTrace();    //TODO: implement fastForeward/fastBackward
            }
        }
    }

    public void setEntriesPerPage(int entries) {
        String inputEntriesPerPageID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2NumRowsInput";

        WebElement inputEntriesPerPage = this.findElement(By.id(inputEntriesPerPageID));
        inputEntriesPerPage.clear();
        inputEntriesPerPage.sendKeys("" + entries);
        inputEntriesPerPage.sendKeys( "\n");
    }

    public void pause(long timeout) {
        synchronized (this) {
            try {
                this.wait(timeout); //wait has to be in synchronised block to work
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public int getMaxPage() {
        return Integer.parseInt(this.getPageInfo().split(" ")[3]);
    }

    public int getCurrentPage() {
        return Integer.parseInt(this.getPageInfo().split(" ")[1]);
    }

    private String getPageInfo() {
        String spanPageTextClass = "dataScrollerPageText";

        return this.findElement(By.className(spanPageTextClass)).getText();
    }
}
