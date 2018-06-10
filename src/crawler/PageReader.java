package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
//import util.Table;

import java.util.Iterator;
import java.util.List;

public class PageReader extends HtmlUnitDriver implements Runnable{

    private int pageNumber, entries;
    private static int shortWait = 100;

    public PageReader(int pageNumber, int entries) {
        super(true);

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
        /*for(int i = 0; i < events.size(); i++) {
            this.openEvent(events.get(i));
            this.getGrundDaten().print();
            Table eventTable = this.getEvents();
            if(eventTable != null) {
                eventTable.print();
                System.out.println(eventTable.getSqlInsertString("EVENTS"));
            }
            Table module = this.getModule();
            if(module != null) {
                module.print();
            }
            this.goBackToEventsPage();
            events = this.readEvents(); //To avoid StaleElementReferenceException...TODO: Find a better way maybe
        }*/
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

        System.out.println("startEmptySearch() done...");
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
        System.out.println("goToPage() done...");
    }

    private void setEntriesPerPage(int entries) {
        String inputEntriesPerPageID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2NumRowsInput";

        WebElement inputEntriesPerPage = this.findElement(By.id(inputEntriesPerPageID));
        inputEntriesPerPage.clear();
        inputEntriesPerPage.sendKeys("" + entries);
        inputEntriesPerPage.sendKeys( "\n");

        System.out.println("setEntriesPerPage() done...");
    }

    private void pause(long timeout) {
        synchronized (this) {
            try {
                this.wait(timeout); //wait has to be in synchronised block to work
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        System.out.println("pause() done...");
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

    /*
    private Table getGrundDaten() {
        String lableHeaderClass = "labelWithBG";
        String divValueClass = "answer";

        //Get Headers:
        String[] headers = getDataArrayById(lableHeaderClass);

        //Initialise Table
        Table grundDaten = new Table(headers);

        //Get Values
        String[] values = getDataArrayById(divValueClass);

        //Add Values to Table
        grundDaten.add(values);

        System.out.println("getGrundDaten() done...");

        return grundDaten;
    }
    */
    private String[] getDataArrayById(String id) {
        List<WebElement> elements = this.findElements(By.className(id));
        String[] dataArray = new String[elements.size()];
        Iterator<WebElement> it = elements.iterator();
        for(int i = 0; i < dataArray.length; i++) {
            dataArray[i] = it.next().getText();
        }
        return dataArray;
    }

    private void openEvent(WebElement event) {
        String buttonBackButtonID = "showEvent:backButtonTop";

        event.click();
        while(true) {   //TODO: replace this with method to avoid redundancy (like in goBackToEventsPage or every other waiting event.
            try {
                this.findElement(By.id(buttonBackButtonID));
                System.out.println("openEvent() done...");
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
                System.out.println("goBackToEventsPage() done...");
                break;
            } catch (NoSuchElementException e) {
                this.pause(shortWait);
                System.out.println("Go Back wait");
            }
        }
    }

   /* private Table getEvents() {
        String tableEventsId = "showEvent:planelementsOfCurrentTerm:0:termineRauemeFieldset1:plannedDatesTable_:" +
                "plannedDatesTable_Table";
        String thHeadersClass = "tableHeader";
        String tbodyTableBodyId = "showEvent:planelementsOfCurrentTerm:0:termineRauemeFieldset1:plannedDatesTable_:" +
                "plannedDatesTable_Table:tbody_element";

        try {
            WebElement eventsTable = this.findElement(By.id(tableEventsId));
            List<WebElement> headersList = eventsTable.findElements(By.className(thHeadersClass));
            String[] headers = new String[headersList.size()];
            for(int i = 0; i < headersList.size(); i++) {
                headers[i] = headersList.get(i).getText();
            }

            Table events = new Table(headers);

            WebElement tableBody = this.findElement(By.id(tbodyTableBodyId));
            List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
            for(WebElement row : rows) {
                List<WebElement> entries = row.findElements(By.tagName("td"));
                String[] values = new String[entries.size()];
                for(int j = 0; j < values.length; j++) {
                    values[j] = entries.get(j).getText();
                }
                events.add(values);
            }
            return events;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private Table getModule() {
        String tableModulInfoId = "showEvent:unitModulsFieldset:unitModules:unitModulesTable";
        String tbodyTableBodyId = "showEvent:unitModulsFieldset:unitModules:unitModulesTable:tbody_element";

        try {
            WebElement modulTable = this.findElement(By.id(tableModulInfoId));

            Table moduls = new Table(new String[]{"Modulnummer", "Modulkürzel", "Bezeichnung"});

            WebElement tableBody = modulTable.findElement(By.id(tbodyTableBodyId));
            List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
            for(WebElement row : rows) {
                List<WebElement> entries = row.findElements(By.tagName("td"));
                String[] values = new String[entries.size()];
                for(int j = 0; j < values.length; j++) {
                    values[j] = entries.get(j).getText();
                }
                moduls.add(values);
            }

            return moduls;

        } catch(NoSuchElementException e) {
            return null;
        }
    }*/

}
