package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import util.Table;

import java.util.Iterator;
import java.util.List;

public class PageReader extends FirefoxDriver implements Runnable{

    private int pageNumber, entries;
    private static int shortWait = 100;

    public PageReader(int pageNumber, int entries) {
        //super(true);

        String MAIN_MARVIN_URL = "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/" +
                "coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1";

        this.pageNumber = pageNumber;
        this.entries = entries;
        this.get(MAIN_MARVIN_URL);
    }

    @Override
    public void run() {
        this.startEmptySearch();
        this.setEntriesPerPage(entries);
        this.goToPage();
        List<WebElement> events = this.readEvents();
        for(WebElement event : events) {
            this.openEvent(event);
            this.getGrundDaten().print();
            this.goBackToEventsPage();
        }
    }

    private List<WebElement> readEvents() {
        String buttonEventLinkClass = "linkTable";

        return this.findElements(By.className(buttonEventLinkClass));
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

    private void goToPage(){
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
                this.pause(shortWait);
            } catch(NoSuchElementException e) {
                e.printStackTrace();    //TODO: implement fastForeward/fastBackward
            }
        }
    }

    private void setEntriesPerPage(int entries) {
        String inputEntriesPerPageID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2NumRowsInput";

        WebElement inputEntriesPerPage = this.findElement(By.id(inputEntriesPerPageID));
        inputEntriesPerPage.clear();
        inputEntriesPerPage.sendKeys("" + entries);
        inputEntriesPerPage.sendKeys( "\n");
    }

    private void pause(long timeout) {
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

    private int getCurrentPage() {
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
            values[i] = it.next().getText();
        }

        //Add Values to Table
        grundDaten.add(values);

        return grundDaten;
    }

    private void openEvent(WebElement event) {
        String buttonBackButtonID = "showEvent:backButtonTop";

        event.click();
        while(true) {   //TODO: replace this with method to avoid redundancy (like in goBackToEventsPage or every other waiting event.
            try {
                this.findElement(By.id(buttonBackButtonID));
                return;  //--> Page is opened
            } catch (org.openqa.selenium.NoSuchElementException e) {
                this.pause(shortWait);
                System.out.println("Open Event wait");
            }
        }
    }

    private void goBackToEventsPage() {
        String buttonNewSearchID = "genSearchRes:buttonsTop:newSearch";
        String buttonBackButtonID = "showEvent:backButtonTop";

        WebElement buttonBackButton = this.findElement(By.id(buttonBackButtonID));
        buttonBackButton.click();

        while(true) {   //TODO replace with methode like in openEvent()
            try {
                this.findElement(By.id(buttonNewSearchID));
            } catch (NoSuchElementException e) {
                this.pause(shortWait);
                System.out.println("Go Back wait");
            }
        }
    }

}
