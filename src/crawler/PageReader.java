package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import util.Table;

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

    public PageReader(int pageNumber) {
        super(true);
        this.pageNumber = pageNumber;
        this.get(MAIN_MARVIN_URL);
    }

    public void readEvents() {
        events = this.findElements(By.className(eventClassName));
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
                    this.pause(100);
                }
                clicked = true;
            } catch(ElementClickInterceptedException e) {
                this.pause(500);
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

    private Table getGrundDaten() {
        String lableHeaderClass = "labelWithBG";
        String divValueClass = "answer";

        //TODO: implement Methode to avoid redundancy in Get Headers and Get Values
        //Get Headers:
        List<WebElement> headersList = this.findElements(By.className(lableHeaderClass));
        String[] headers = new String[headersList.size()];
        // TODO: Check runntime: List Iteration vs List.get(index)
        Iterator<WebElement> it = headersList.iterator();
        for(int i = 0; i < headers.length; i++) {
            headers[i] = it.next().getText();
        }

        //Initialise Table
        Table grundDaten = new Table(headers);

        //Get Values
        List<WebElement> valuesList= this.findElements(By.className(divValueClass));
        String[] values = new String[valuesList.size()];
        it = valuesList.iterator();
        for(int i = 0; i < values.length; i++) {
            values[i] =it.next().getText();
        }

        //Add Values to Table
        grundDaten.add(values);

        return grundDaten;
    }
}
