package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;

/**
 * @author Jakob Eckstein
 *
 */
public class PageNavigator extends HtmlUnitDriver {

    public PageNavigator() {
        super(true); //to enable JavaScript support of the HtmlUnitDriver
    }

    public void test(int pagenumber, int entriesPerPage) {
        this.openMarvinSearch();
        System.out.println(this.getTitle());
        this.startEmptySearch();
        System.out.println(this.getMaxPage());
        this.setEntriesPerPage(entriesPerPage);
        System.out.println(this.getMaxPage());
        this.goToPage(pagenumber);
        for(int i = 0; i < entriesPerPage; i++) {

            this.openEvent(this.getEvent(i));
            System.out.println(this.getTitle());

            this.eventBack();
        }
    }

    /**
     * Opens the main Marvin search page in this instance.
     * @link "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1"
     */
    public void openMarvinSearch() {
        //TODO: test
        String MAIN_MARVIN_URL = "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourse" +
                "Data.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1";

        this.get(MAIN_MARVIN_URL);
    }

    /**
     * Starts a empty search in this instance to find all Marvin entries.
     */
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

    /**
     * @param entriesPerPage Number of entries per Page. TODO: Maybe find a better description???
     * Sets the entries per Page to the specified value.
     */
    public void setEntriesPerPage(int entriesPerPage) {//keinen parameter
        //TODO: test
        String inputEntriesPerPageID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2NumRowsInput";

        //Clear entries field:
        WebElement inputEntriesPerPage = this.findElement(By.id(inputEntriesPerPageID));
        inputEntriesPerPage.clear();
        //Enter entries and start querry:
        inputEntriesPerPage.sendKeys("" + entriesPerPage);
        inputEntriesPerPage.sendKeys( "\n");
    }

    /**
     * @param pageNumber Number of searchpage to navigate to.
     * Navigates to the specified searchpage and waits until the Page is loaded.
     */
    public void goToPage(int pageNumber) {
        //TODO: test
        String aPageLinkID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2idx" + pageNumber;
        String aFastForwardButtonID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2fastf";

        boolean done = false;

        while(!done) {
            try {
                this.findElement(By.id(aPageLinkID)).click();
                while(this.getCurrentPage() != pageNumber){
                    this.pause(100);
                }
                done = true;    //
            } catch(ElementClickInterceptedException e) {   //this exeption is thrown when the click() Methode fails.
                this.pause(100);
            } catch(NoSuchElementException e) {
                this.findElement(By.id(aFastForwardButtonID)).click();//TODO: TEST!!!
            }
        }
    }

    //TODO: Add javadoc and test
    private void pause(long milliseconds) {
        synchronized (this) {
            try {
                this.wait(milliseconds); //wait has to be in synchronised block to work
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    /**
     *
     * @return List of all event links on the curently opend page.
     */
    public List<WebElement> getEvents() {
        //TODO: test.
        String buttonEventLinkClass = "linkTable";

        return this.findElements(By.className(buttonEventLinkClass));
    }

    public WebElement getEvent(int index) {
        List<WebElement> list = this.getEvents();
        return list.get(index);
    }

    /**
     * @param eventLink Link to the event.
     * Opens an event int this instance.
     */
    public void openEvent(WebElement eventLink) {
        //TODO: test
        String buttonBackButtonID = "showEvent:backButtonTop";

        //clicks the eventLink and waits until the Back-Button is found
        eventLink.click();
        this.waitForElement(buttonBackButtonID);
    }

    /**
     * Navigates back to the searchpage.
     */
    public void eventBack() {
        String buttonNewSearchID = "genSearchRes:buttonsTop:newSearch";
        String buttonBackButtonID = "showEvent:backButtonTop";

        //clicks the Back-Button
        WebElement buttonBackButton = this.findElement(By.id(buttonBackButtonID));
        buttonBackButton.click();
        //waits until the NewSearch button is found.
        this.waitForElement(buttonNewSearchID);
    }

    private void waitForElement(String id){
        long start = System.nanoTime();
        boolean warnOnce = false;
        boolean printSourceOnce = false;
        while((System.nanoTime() - start) < 90_000_000_000L) {
            try {
                this.findElement(By.id(id));
                return;
            } catch (org.openqa.selenium.NoSuchElementException e) {
                if((System.nanoTime() - start) > 30_000_000_000L &! warnOnce){
                    warnOnce = true;
                    System.out.printf("Wait For Element Warning (30sec): %s\n", id);
                }
                if((System.nanoTime() - start) > 60_000_000_000L &! printSourceOnce){
                    printSourceOnce = true;
                    System.out.printf("Wait For Element Warning (1min): %s\n", id);
                    System.out.println(this.getPageSource());
                }
                this.pause(101);
            }
        }
        throw new RuntimeException("Wait For Element Timeout (1min 30sec). Element: " + id + " not found");
    }

    /**
     * @return String containing the HTML Source Code of the current page.
     * Returns the HTML Source Code of the Page that is currently opened in this WebDriver instance.
     */
    public String getEventSource() {//TODO: Do we really need this?
        return this.getPageSource();
    }

    public int getMaxPage() {
        return Integer.parseInt(this.getPageInfo().split(" ")[3]);
    }

    private int getCurrentPage() {
        return Integer.parseInt(this.getPageInfo().split(" ")[1]);
    }

    private String getPageInfo() {
        String spanPageTextClass = "dataScrollerPageText";

        return this.findElement(By.className(spanPageTextClass)).getText();
    }
    public static void main(String[] args){
        PageNavigator pageNavigator = new PageNavigator();
        pageNavigator.openMarvinSearch();
        pageNavigator.startEmptySearch();
    }
}