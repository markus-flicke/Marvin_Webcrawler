package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import util.EventData;

import java.util.*;

/**
 * @author Jakob Eckstein
 *
 */
public class EventReader {
    WebDriver driver;
    EventReader(WebDriver driver){
        this.driver = driver;
    }

    /**
     * @return Key-Value Map containing the basic data of the currently opened Event.
     */
    private String[][] getBasicData() {
        String lableHeaderClass = "labelWithBG";
        String divValueClass = "answer";
        String[] headers = this.getDataArrayById(lableHeaderClass);
        String[] values = this.getDataArrayById(divValueClass);
        return new String[][] {headers, values};
    }

    /**
     * @return String[][] containing the event table of the currently opened Event.
     */
    private String[][] getEventTable() {
        String tableEventsId = "showEvent:planelementsOfCurrentTerm:0:termineRauemeFieldset1:plannedDatesTable_:" +
                "plannedDatesTable_Table";
        String thHeadersClass = "tableHeader";
        String tbodyTableBodyId = "showEvent:planelementsOfCurrentTerm:0:termineRauemeFieldset1:plannedDatesTable_:" +
                "plannedDatesTable_Table:tbody_element";

        WebElement eventsTable = driver.findElement(By.id(tableEventsId));
        List<WebElement> headersList = eventsTable.findElements(By.className(thHeadersClass));
        String[] headers = new String[headersList.size()];
        for(int i = 0; i < headersList.size(); i++) {
            headers[i] = headersList.get(i).getText();
        }

        LinkedList<String[]> values = new LinkedList<String[]>();

        WebElement tableBody = driver.findElement(By.id(tbodyTableBodyId));
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        for(WebElement row : rows) {
            List<WebElement> entries = row.findElements(By.tagName("td"));
            String[] valueTupel = new String[entries.size()];
            for(int j = 0; j < valueTupel.length; j++) {
                valueTupel[j] = entries.get(j).getText();
            }
            values.add(valueTupel);
        }
        String[][] res = new String[values.size() + 1][headers.length];
        res[0] = headers;
        for (int i = 0; i< values.size(); i++){
            res[i+1] = values.get(i);
        }
        return res;
    }

    /**
     * @return String[][] containing the module table of the currently opened Event.
     */
    private String[][] getModuleTable() {
        String tableModulInfoId = "showEvent:unitModulsFieldset:unitModules:unitModulesTable";
        String tbodyTableBodyId = "showEvent:unitModulsFieldset:unitModules:unitModulesTable:tbody_element";


        WebElement modulTable = driver.findElement(By.id(tableModulInfoId));

        String[] headers = new String[]{"Modulnummer", "Modulkürzel", "Bezeichnung"};

        LinkedList<String[]> values = new LinkedList<String[]>();

        WebElement tableBody = modulTable.findElement(By.id(tbodyTableBodyId));
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        for(WebElement row : rows) {
            List<WebElement> entries = row.findElements(By.tagName("td"));
            String[] valueTupel = new String[entries.size()];
            for(int j = 0; j < valueTupel.length; j++) {
                valueTupel[j] = entries.get(j).getText();
            }
            values.add(valueTupel);
        }
        String[][] res = new String[1 + values.size()][headers.length];
        res[0] = headers;
        for (int i = 0; i< values.size(); i++){
            res[i+1] = values.get(i);
        }
        return res;
    }


    /**
     * @return EventData object ready to get uploaded to database.
     */
    public EventData getEventData() {
        return new EventData(getBasicData(), getEventTable(), getModuleTable());
    }

    private String[] getDataArrayById(String id) {
        List<WebElement> elements = driver.findElements(By.className(id));
        String[] dataArray = new String[elements.size()];
        Iterator<WebElement> it = elements.iterator();
        for(int i = 0; i < dataArray.length; i++) {
            dataArray[i] = it.next().getText();
        }
        return dataArray;
    }

    public static void main(String[] args){
        HtmlUnitDriver driver = new HtmlUnitDriver(true);
        driver.get("https://marvin.uni-marburg.de:443/qisserver/pages/startFlow.xhtml?_flowId=showEvent-flow&unitId=16751&termYear=2018&termTypeValueId=30&navigationPosition=studiesOffered,searchCourses");
        EventReader eventReader = new EventReader(driver);
        EventData res = eventReader.getEventData();
        System.out.print(res);
    }
}