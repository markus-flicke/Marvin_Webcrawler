package util;

import java.util.HashMap;

/**
 * @author Jakob Eckstein
 *
 */
public class EventData {
    private HashMap<String, String> basicData;
    private String[][] eventTable;
    private String[][] moduleTable;

    public EventData(HashMap<String, String> basicData, String[][] eventTable, String[][] moduleTable) {
        this.basicData = basicData;
        this.eventTable = eventTable;
        this.moduleTable = moduleTable;
    }
}
