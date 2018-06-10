package crawler;

import org.openqa.selenium.WebDriver;
import util.EventData;

import java.util.HashMap;

/**
 * @author Jakob Eckstein
 *
 */
public class EventReader {

    /**
     * @param driver WebDriver to read the basic data from.
     * @return Key-Value Map containing the basic data of the currently opened Event.
     */
    private HashMap<String, String> getBasicData(WebDriver driver) {
        //TODO: implement
        return null;
    }
    /**
     * @param source HTML source code to read the basic data from.
     * @return Key-Value Map containing the basic data of the Event described by source.
     */
    private HashMap<String, String> getBasicData(String source) {
        //TODO: implement
        return null;
    }

    /**
     * @param driver WebDriver to read the event table from.
     * @return String[][] containing the event table of the currently opened Event.
     */
    private String[][] getEventTable(WebDriver driver) {
        //TODO: implement
        return null;
    }
    /**
     * @param source HTML source code to read the event table from.
     * @return String[][] containing the event table of the Event described by source.
     */
    private String[][] getEventTable(String source) {
        //TODO: implement
        return null;
    }

    /**
     * @param driver WebDriver to read the module table from.
     * @return String[][] containing the module table of the currently opened Event.
     */
    private String[][] getModuleTable(WebDriver driver) {
        //TODO: implement
        return null;
    }
    /**
     * @param source HTML source code to read the module table from.
     * @return String[][] containing the module table of the Event described by source.
     */
    private String[][] getModuleTable(String source) {
        //TODO: implement
        return null;
    }

    /**
     * @return EventData object ready to get uploaded to database.
     */
    public EventData getEventData() {
        //TODO: implement
        return null;
    }
}
