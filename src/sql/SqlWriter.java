package sql;

import util.EventData;

import java.sql.*;

//TODO: TEST and Refactoring
public class SqlWriter {
    private final static String user = "postgres", password = "somthing", url = "jdbc:postgresql://localhost/Vorlesungsverzeichnis";
    private EventData data;
    private Connection connection;
    public static int Veranstaltungsnummer = 1;

    public SqlWriter(EventData data, Connection connection) {
        this.data = data;
        this.connection = connection;
    }

    private int getVeranstaltungsnummer() {
        return 0;
    }

    private void uploadVeranstaltung() {
        String[][] basicData = this.data.getBasicData();

        int verantwortlicherIndex = this.getIndex("Verantwortliche/-r", basicData);
        int organisationseinheitIndex = this.getIndex("Organisationseinheit", basicData);
        int titelIndex = this.getIndex("Titel", basicData);

        String sqlQuerry = "INSERT INTO Veranstaltungen" +
                "(verantwortlicher, organisationseinheit, titel)" +
                "VALUES ('";
        sqlQuerry += this.data.getBasicData()[1][verantwortlicherIndex] + "', '"
                + this.data.getBasicData()[1][organisationseinheitIndex] + "', '"
                + this.data.getBasicData()[1][titelIndex] + "');";
        this.upload(sqlQuerry);
    }

    private int getIndex(String columnName, String[][] table) {
        for(int i = 0; i < table[0].length;i++) {
            if(columnName == table[0][i]) {
                return i;
            }
        }
        throw new RuntimeException("Das gesuchte Attribut("+columnName+") konnte nicht gefunden werden.");
    }

    private void uploadModule() {
        String[][] modulTable = this.data.getModuleTable();
        int modulnummerIndex = getIndex("Modulnummer", modulTable);
        int modulkuerzelIndex = getIndex("Modulkürzel", modulTable);
        int bezeichnungIndex = getIndex("Bezeichnung", modulTable);
        String sqlQuery = "INSERT INTO Module "+
                "(modulID, modulkuerzel, bezeichnung) "+
                "VALUES ";

        for(int i = 1; i < modulTable.length; i++) {
            sqlQuery += "("+ modulTable[i][modulnummerIndex] + ", '" +
                    modulTable[i][modulkuerzelIndex] + "', '" +
                    modulTable[i][bezeichnungIndex]+ "')";
            if(i != modulTable.length - 1) {
                sqlQuery += ", ";
            }
            sqlQuery+=";";
        }
        this.upload(sqlQuery);
    }

    private void uploadEvents() {
        String[][] eventTable = this.data.getEventTable();
        int veranstaltungsID = this.getVeranstaltungsnummer();//TODO: Implement
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
        String sqlQuery = "INSERT INTO Events (veranstaltungsID, wochentag, von, bis, akademischezeit," +
                "rhythmus, startdatum , enddatum, teilnehmerzahl , raum , durchführender , ausfalltermin , " +
                "bemerkung ) VALUES";

        for(int i = 0; i < eventTable.length; i++) {
            sqlQuery += "("+ veranstaltungsID + ", '" + eventTable[i][wochentagIndex] + "', '" +
                  eventTable[i][vonIndex] + "', '" + eventTable[i][bisIndex] + "', '" +
                    eventTable[i][akademischezeitIndex] + "', '" + eventTable[i][rhythmusIndex] + "', '" +
                    eventTable[i][startDatumIndex] + "', '" + eventTable[i][endDatumIndex] + "', '" +
                    eventTable[i][teilnemherzahlIndex] + "', '" + eventTable[i][raumIndex]  + "', '" +
                    eventTable[i][durchführenderIndex] + "', '" + eventTable[i][ausfallterminIndex] + "', '" +
                    eventTable[i][bemerkungIndex]+"')";
            if(i != eventTable.length - 1) {
                sqlQuery += ", ";
            }
            sqlQuery+=";";
        }
        this.upload(sqlQuery);
    }

    private void uploadModulzuteilung() {
        String[][] modulTable = this.data.getModuleTable();
        int modulnummerIndex = getIndex("Modulnummer", modulTable);
        int eventId = 0;
        //VeranstaltungsID ist schon bekannt.
        //TODO: SQL anfrage stellen um die event_Ids zu ermitteln

        String sqlQuerry = "INSERT INTO Modulzuteilung (modulnummer, veranstaltungsnummer) VALUES ";
        //TODO: Für jedes Modul aus der Modul Tabelle und jedes Event aus der Event Tabelle ein Tupel einfügen.
        for(int i = 1; i < modulTable.length; i++) {
            sqlQuerry += "(" + modulTable[i][modulnummerIndex] + ", " + eventId + ")";
            if(i != modulTable.length - 1) {
                sqlQuerry += ", ";
            }
        }
        this.upload(sqlQuerry);
    }

    private void upload(String sqlQuery) {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            throw new RuntimeException("Upload nicht möglich. Insertstatment:\n" + sqlQuery +
                    "\nkonnte nicht ausgeführt werden.");
        }
    }
}