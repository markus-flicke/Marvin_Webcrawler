package crawler;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.util.List;
import java.time.Duration;

/**
 * @author Jakob Eckstein
 *
 */
public class PageNavigator extends HtmlUnitDriver{

    final int ENTRIES_PER_PAGE = 10;
    FluentWait<PageNavigator> wait;
    int currentPage;

    public PageNavigator() {
        super(true); //to enable JavaScript support of the HtmlUnitDriver
        wait = new FluentWait<>(this)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Opens the main Marvin search page in this instance.
     * @link "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1"
     * Starts a empty search in this instance to find all Marvin entries.
     */
    public void init() {
        this.openMarvinSearch();    //Opens the main Marvin search page in this instance.
        this.startEmptySearch();    //Starts a empty search in this instance to find all Marvin entries.
    }
    private void openMarvinSearch() {
        String MAIN_MARVIN_URL = "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourse" +
                "Data.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1";

        this.get(MAIN_MARVIN_URL);
    }
    private void startEmptySearch() {
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

   /*
    public void setEntriesPerPage(int entriesPerPage) {//keinen parameter
        String inputEntriesPerPageID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2NumRowsInput";

        //Clear entries field:
        WebElement inputEntriesPerPage = this.findElement(By.id(inputEntriesPerPageID));
        inputEntriesPerPage.clear();
        //Enter entries and start querry:
        inputEntriesPerPage.sendKeys("" + entriesPerPage);
        inputEntriesPerPage.sendKeys( "\n");
    }*/

    /**
     * @param pageNumber Number of searchpage to navigate to.
     * Navigates to the specified searchpage and waits until the Page is loaded.
     */
    public void goToPage(int pageNumber) throws TimeoutException {
        currentPage = getCurrentPage();
        JavascriptExecutor je = (JavascriptExecutor) this;
        je.executeScript(
                "var event = new Event('onclick');" +
                        "jsf.util.chain(document.getElementById('genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2idx"+
                        currentPage + "')," +
                        " event," +
                        "'jsf.ajax.request(\\'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2idx"+
                        currentPage +"\\'," +
                        "event," +
                        "{execute:\\'genSearchRes:id3df798d58b4bacd9 genSearchRes \\'," +
                        "render:\\'genSearchRes:id3df798d58b4bacd9 genSearchRes genSearchRes:messages-infobox \\'," +
                        "onerror:de.his.ajax.Refresher.onError,onevent:de.his.ajax.Refresher.onEvent," +
                        "\\'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2\\':\\'idx"+ pageNumber + "\\'," +
                        "\\'javax.faces.behavior.event\\':\\'action\\'})');");
        //try {
        wait.until((PageNavigator pn) -> pn.getCurrentPage() == pageNumber);//} catch(TimeoutException toe) {goToPage(pageNumber);};
        currentPage = getCurrentPage();
    }


    /*public void goToPage(int pageNumber) {
        String aPageLinkID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2idx" + pageNumber;
        String aFastForwardButtonID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2fastf";
        Wait<PageNavigator> wait = new FluentWait<>(this)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(NoSuchElementException.class);
        boolean done = false;
        while(!done) {
            try {
                this.findElement(By.id(aPageLinkID)).click();
                while(this.getCurrentPage() != pageNumber){
                    this.pause(WAIT_TIME);
                }
                done = true;
            } catch(ElementClickInterceptedException e) {   //this exeption is thrown when the click() Methode fails.
                this.pause(WAIT_TIME);
            } catch(NoSuchElementException e) {
                System.out.println("Page: " + this.getCurrentPage());
                fastForward();
            }
        }
    }
    private void fastForward() {
        String aFastForwardButtonID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2fastf";

        WebElement ffButton = wait.until((WebDriver pn) -> pn.findElement(By.id(aFastForwardButtonID)));
        ffButton.click();

    }*/

    /*private void pause(long milliseconds) {
        synchronized (this) {
            try {
                this.wait(milliseconds); //wait has to be in synchronised block to work
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }*/

    /*private List<WebElement> getEvents() {
        String buttonEventLinkClass = "linkTable";

        return this.findElements(By.className(buttonEventLinkClass));
    }

    public WebElement getEvent(int index, int test) {
        List<WebElement> list = this.getEvents();
        return list.get(index);
    }*/


    public void openEvent(int index) {
        WebElement eventLink = getEvent(index);
        eventLink.click();
        //System.out.println(this.getTitle());
        wait.until((PageNavigator pn) -> {
            String title = pn.getTitle();
            return (title == null)?false:title.contains("Veranstaltungsdaten");
        });
        /*while(true) {
            try {
                this.findElement(By.id("genSearchRes:buttonsTop:newSearch"));
                //this.pause(WAIT_TIME);
            } catch (org.openqa.selenium.NoSuchElementException e) {
                System.out.println(this.getTitle());
                break;
            }
        }*/
    }
    private WebElement getEvent(int index) {
        int eventNumber = (currentPage - 1)* ENTRIES_PER_PAGE + index;
        String buttonEventLinkID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Table:"+ eventNumber +":actionsLeft:show";

        return wait.until((PageNavigator pn) -> pn.findElement(By.id(buttonEventLinkID)));
    }

    /**
     * Navigates from the event page to the searchpage.
     */
    public void eventBack() {
        //String buttonNewSearchID = "genSearchRes:buttonsTop:newSearch";
        String buttonBackButtonID = "showEvent:backButtonTop";

        //clicks the Back-Button
        WebElement buttonBack = wait.until((PageNavigator pn) -> pn.findElement(By.id(buttonBackButtonID)));
        buttonBack.click();
        //waits until the NewSearch button is found.
        try {
            wait.until((PageNavigator pn) -> {
                String title = pn.getTitle();
                return (title == null) ? false : title.contains("Veranstaltungen suchen");
            });
        } catch (TimeoutException toe) {
            System.out.println("Aktueller Title: " + getTitle());
            throw toe;
        }
    }

   /* private void waitForElement(String id){
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
                this.pause(WAIT_TIME);
            }
        }
        throw new RuntimeException("Wait For Element Timeout (1min 30sec). Element: " + id + " not found");
    }*/

    public int getMaxPage() {
        return Integer.parseInt(this.getPageInfo().split(" ")[3]);
    }
    private int getCurrentPage() {
        return Integer.parseInt(this.getPageInfo().split(" ")[1]);
    }
    private String getPageInfo() {
        String spanPageTextClass = "dataScrollerPageText";
        String pageInfo = wait.until((PageNavigator pn) -> pn.findElement(By.className(spanPageTextClass)).getText());
        return pageInfo;
    }
}