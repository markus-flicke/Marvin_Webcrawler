package util;

import java.util.HashMap;

/**
 * @author Jakob Eckstein
 *
 */
public class EventData {
    private String[][] eventTable;
    private String[][] moduleTable;
    private String[][] basicData;

    public EventData(String[][] basicData, String[][] eventTable, String[][] moduleTable) {
        this.basicData = basicData;
        this.eventTable = eventTable;
        this.moduleTable = moduleTable;
    }

    public String[][] getBasicData() {
        return basicData;
    }

    public String[][] getEventTable() {
        return eventTable;
    }

    public String[][] getModuleTable() {
        return moduleTable;
    }

    public void setBasicData(String[][] basicData) {
        this.basicData = basicData;
    }

    public void setEventTable(String[][] eventTable) {
        this.eventTable = eventTable;
    }

    public void setModuleTable(String[][] moduleTable) {
        this.moduleTable = moduleTable;
    }

    public String toString(){
        String res = "Basic Data:\n";
        for(String[] row: basicData) {
            for (String item : row) {
                res += item + ", ";
            }
            res += "\n";
        }
        res += "\n\n\n";

        res += "Event Data:\n";
        for(String[] row: eventTable) {
            for (String item : row) {
                res += item + ", ";
            }
            res += "\n";
        }
        res += "\n\n\n";

        res += "Modul Data:\n";
        for(String[] row: moduleTable) {
            for (String item : row) {
                res += item + ", ";
            }
            res += "\n";
        }
        res += "\n\n\n";
        return res;
    }


}
