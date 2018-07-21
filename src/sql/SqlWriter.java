package sql;

import util.EventData;

import java.sql.*;
import java.util.LinkedList;

public class SqlWriter {
    private EventData data;
    private Connection connection;
    public static int Veranstaltungsnummer = 1;
    int organisationseinheitIndex, verantwortlicherIndex, titelIndex;
    int veranstaltungsID;
    LinkedList<Integer> eventIDs = new LinkedList<>();

    public SqlWriter(EventData data, Connection connection) {
        this.data = data;
        this.connection = connection;

        setIndeces();
    }

    private void setIndeces(){
        String[][] basicData = this.data.getBasicData();
        organisationseinheitIndex = this.getIndex("Organisationseinheit", basicData);
        verantwortlicherIndex = this.getIndex("Verantwortliche/-r", basicData);
        titelIndex = this.getIndex("Titel", basicData);
    }

    public void uploadAll(){
        uploadModule();
        uploadVeranstaltung();
        uploadEvents();
        uploadEMzuteilung();
    }

    private void getVeranstaltungsnummer() {
        ResultSet res = query(String.format("Select veranstaltungsid from veranstaltungen where titel = '%s'",
                this.data.getBasicData()[1][titelIndex]));
        try{
            res.next();
            veranstaltungsID = res.getInt("veranstaltungsid");
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private int getmaxEventID(){
        ResultSet res = query(String.format("Select max(eventid) as maxID from events",
                this.data.getBasicData()[1][titelIndex]));
        try{
            res.next();
            int maxEID = res.getInt("maxID");
            return maxEID;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void uploadVeranstaltung() {
        String sqlQuery = String.format("INSERT INTO Veranstaltungen (verantwortlicher, organisationseinheit, titel) " +
                        "Select '%s', '%s', '%s'",
                this.data.getBasicData()[1][verantwortlicherIndex],
                this.data.getBasicData()[1][organisationseinheitIndex],
                this.data.getBasicData()[1][titelIndex]);
        sqlQuery += String.format("WHERE NOT EXISTS (SELECT * FROM Veranstaltungen WHERE titel = '%s');",
                this.data.getBasicData()[1][titelIndex]);
        this.upload(sqlQuery);
    }

    private int getIndex(String columnName, String[][] table) {
        for(int i = 0; i < table[0].length;i++) {
            if(columnName.equals(table[0][i])) {
                return i;
            }
        }
        throw new RuntimeException("Das gesuchte Attribut("+columnName+") konnte nicht gefunden werden.");
    }

    public void uploadModule() {
        String[][] modulTable = this.data.getModuleTable();
        int modulnummerIndex = getIndex("Modulnummer", modulTable);
        int modulkuerzelIndex = getIndex("Modulkürzel", modulTable);
        int bezeichnungIndex = getIndex("Bezeichnung", modulTable);

        for(int i = 1; i < modulTable.length; i++) {
            String sqlQuery = String.format("INSERT INTO Module(modulID, modulkuerzel, bezeichnung) Select %s,'%s','%s' "
                    ,modulTable[i][modulnummerIndex], modulTable[i][modulkuerzelIndex],modulTable[i][bezeichnungIndex]);
            sqlQuery += String.format("WHERE NOT EXISTS (SELECT * FROM Module WHERE modulID = CAST(%s as BIGINT));",modulTable[i][modulnummerIndex]);
            this.upload(sqlQuery);
        }

    }

    private LinkedList<Integer> uploadEvents() {
//        Returns array of event ids that have been uploaded
        String[][] eventTable = this.data.getEventTable();
        int maxEventID = getmaxEventID();
        int vonIndex = getIndex("Von", eventTable);
        int bisIndex = getIndex("Bis", eventTable);
        int wochentagIndex = getIndex("Wochentag", eventTable);
        int rhythmusIndex = getIndex("Rhythmus", eventTable);
        int akademischezeitIndex = getIndex("Akad. Zeit", eventTable);
        int startDatumIndex = getIndex("Startdatum", eventTable);
        int endDatumIndex = getIndex("Enddatum", eventTable);
        int teilnemherzahlIndex = getIndex("Erwartete Anzahl Teilnehmer/-innen", eventTable);
        int raumIndex = getIndex("Raum", eventTable);
        int durchführenderIndex = getIndex("Durchführende/-r", eventTable);
        int ausfallterminIndex = getIndex("Ausfalltermin", eventTable);
        int bemerkungIndex = getIndex("Bemerkung", eventTable);
        String sqlQuery = "INSERT INTO Events (eventID, veranstaltungsID, wochentag, von, bis, akademischezeit," +
                "rhythmus, startdatum , enddatum, teilnehmerzahl , raum , durchführender , ausfalltermin , " +
                "bemerkung ) VALUES";
        getVeranstaltungsnummer();
        for(int i = 1; i < eventTable.length; i++) {
            maxEventID++;
            eventIDs.add(maxEventID);
            sqlQuery += String.format("(%d, %d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                    maxEventID,
                    veranstaltungsID,
                    eventTable[i][wochentagIndex],
                    eventTable[i][vonIndex],
                    eventTable[i][bisIndex],
                    eventTable[i][akademischezeitIndex],
                    eventTable[i][rhythmusIndex],
                    eventTable[i][startDatumIndex],
                    eventTable[i][endDatumIndex],
                    eventTable[i][teilnemherzahlIndex],
                    eventTable[i][raumIndex],
                    eventTable[i][durchführenderIndex],
                    eventTable[i][ausfallterminIndex],
                    eventTable[i][bemerkungIndex]
                    );
            if(i != eventTable.length - 1) {
                sqlQuery += ", ";
            }
        }
        sqlQuery+=";";
        this.upload(sqlQuery);
        return eventIDs;
    }

    private void uploadEMzuteilung() {
        String[][] modulTable = this.data.getModuleTable();
        String[][] eventTable = this.data.getEventTable();
        int modulnummerIndex = getIndex("Modulnummer", modulTable);
        String sqlQuery = "INSERT INTO EMzuteilung (ModulID, EventID) VALUES ";
        int eventCounter = 0;
        for(int eventId : eventIDs){
            for(int i = 1; i < modulTable.length; i++) {
                sqlQuery += String.format("('%s', %d)",
                        modulTable[i][modulnummerIndex],
                        eventId
                        );
                if(i != modulTable.length - 1) {
                    sqlQuery += ", ";
                }
            }
            eventCounter++;
            if(eventCounter != eventTable.length - 1) {
                sqlQuery += ", ";
            }
        }
        this.upload(sqlQuery);
    }

    private void upload(String sqlQuery) {
        try {
            Statement stmt = connection.createStatement();
            int res = stmt.executeUpdate(sqlQuery);
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Upload nicht möglich. InsertStatement:");
            System.out.println(sqlQuery);

            throw new RuntimeException(e);
        }
    }

    private ResultSet query(String sqlQuery){
        try {
            Statement stmt = connection.createStatement();
            ResultSet res = stmt.executeQuery(sqlQuery);
            connection.commit();
            return res;
        } catch (SQLException e) {
            System.out.println("Query not possible. Query:");
            System.out.println(sqlQuery);
            throw new RuntimeException(e);
        }
    }
}