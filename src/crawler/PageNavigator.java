package crawler;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * @author Jakob Eckstein
 *
 */
public class PageNavigator extends HtmlUnitDriver {

    public PageNavigator() {
        super(true); //to enable JavaScript support of the HtmlUnitDriver
    }

    /**
     * Opens the main Marvin search page in this instance.
     * @link "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1"
     */
    private void openMarvinSearch() {
        //TODO: implement
        String MAIN_MARVIN_URL = "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourse" +
                "Data.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1";
        this.get(MAIN_MARVIN_URL);
    }

    /**
     * Starts a empty search in this instance to find all Marvin entries.
     */
    private void startEmptySearch() {
        //TODO: implement
    }

    /**
     * @param entriesPerPage Number of entries per Page. TODO: Maybe find a better description???
     * Sets the entries per Page to the specified value.
     */
    private void setEntriesPerPage(int entriesPerPage) {//keinen parameter
        //TODO: implement
    }

    /**
     * @param pageNumber Number of searchpage to navigate to.
     * Navigates to the specified searchpage.
     */
    private void goToPage(int pageNumber) {
        //TODO: implement
    }

    /**
     * @param eventLink Link to the event.
     * Opens an event int this instance.
     */
    private void openEvent(WebElement eventLink) {
        //TODO: implement
    }

    /**
     * Navigates back to the searchpage.
     */
    private void goBackToPage() {//TODO: Think about a better name for this method
        //TODO: implement
    }

    /**
     * @return String containing the HTML Source Code of the current page.
     * Returns the HTML Source Code of the Page that is currently opened in this WebDriver instance.
     */
    public String getEventSource() {//TODO: Do we really need this?
        //TODO: implement
        return null;
    }

    /**
     * @return This WebDriver instance.
     * Returns this WebDriver instance.
     */
    public PageNavigator getDriver() {//TODO: Does "return this" work?
        //TODO: implement
        return this;
    }

    /**
     * @version 2.0
     * @return
     */
    public int getMaxPage() {
        //TODO: implement
        return 0;
    }
}
