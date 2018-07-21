package crawler;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import util.EventData;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jakob Eckstein
 *
 */
public class EventReader {
    WebDriver driver;

    public EventReader(WebDriver driver){
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
//        String tbodyTableBodyId = "showEvent:planelementsOfCurrentTerm:0:termineRauemeFieldset1:plannedDatesTable_:" +
//                "plannedDatesTable_Table:tbody_element";

        try {
            WebElement eventsTable = driver.findElement(By.id(tableEventsId));
            List<WebElement> headersList = eventsTable.findElements(By.className(thHeadersClass));
            String[] headers = new String[headersList.size()];
            for (int i = 0; i < headersList.size(); i++) {
                headers[i] = headersList.get(i).getText();
            }

            LinkedList<String[]> values = new LinkedList<String[]>();

            List<WebElement> tables = driver.findElements(By.className("tableWithBorder"));
            for (WebElement table : tables) {
                try {
                    table.findElement(By.className("column3"));//Nicht in der Module-Tabelle vorhanden
                } catch(NoSuchElementException e) {
                    continue;
                }
                WebElement tableBody = table.findElement(By.tagName("tbody"));
                List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
                for (WebElement row : rows) {
                    List<WebElement> entries = row.findElements(By.tagName("td"));
                    String[] valueTupel = new String[entries.size()];
                    for (int j = 0; j < valueTupel.length; j++) {
                        valueTupel[j] = entries.get(j).getText();
                    }
                    values.add(valueTupel);
                }
            }
            String[][] res = new String[values.size() + 1][headers.length];
            res[0] = headers;
            for (int i = 0; i < values.size(); i++) {
                res[i + 1] = values.get(i);
            }
            return res;
        } catch(ElementNotFoundException e) {
            return null;
        }
    }

    /**
     * @return String[][] containing the module table of the currently opened Event.
     */
    private String[][] getModuleTable() {
        String tableModulInfoId = "showEvent:unitModulsFieldset:unitModules:unitModulesTable";
        String tbodyTableBodyId = "showEvent:unitModulsFieldset:unitModules:unitModulesTable:tbody_element";
        try {
            WebElement modulTable = driver.findElement(By.id(tableModulInfoId));

            String[] headers = new String[]{"Modulnummer", "Modulkürzel", "Bezeichnung"};

            LinkedList<String[]> values = new LinkedList<String[]>();

            WebElement tableBody = modulTable.findElement(By.id(tbodyTableBodyId));
            List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
            for (WebElement row : rows) {
                List<WebElement> entries = row.findElements(By.tagName("td"));
                String[] valueTupel = new String[entries.size()];
                for (int j = 0; j < valueTupel.length; j++) {
                    valueTupel[j] = entries.get(j).getText();
                }
                values.add(valueTupel);
            }
            String[][] res = new String[1 + values.size()][headers.length];
            res[0] = headers;
            for (int i = 0; i < values.size(); i++) {
                res[i + 1] = values.get(i);
            }
            return res;
        } catch (ElementNotFoundException e) {
            return null;
        }
    }


    /**
     * @return EventData object ready to get uploaded to database.
     */
    public EventData getEventData() {
        return new EventData(getBasicData(), getEventTable(), getModuleTable());
    }

    private String[] getDataArrayById(String id) {
        try {
            List<WebElement> elements = driver.findElements(By.className(id));
            String[] dataArray = new String[elements.size()];
            Iterator<WebElement> it = elements.iterator();
            for (int i = 0; i < dataArray.length; i++) {
                dataArray[i] = it.next().getText();
            }
            return dataArray;
        } catch(Exception e) {
            return null;
        }
    }

    public String getPermalink(){
        String source = driver.getPageSource();
        Pattern pattern = Pattern.compile("data-page-permalink=\"true\">(.*)<\\/textarea");
        Matcher matcher = pattern.matcher(source);
        matcher.find();
        String res = matcher.group(1);
        res = res.replace("&amp;", "&");
        return res;
    }

    public static void main(String[] args){
    }
}