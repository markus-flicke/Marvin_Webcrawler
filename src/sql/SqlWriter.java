package sql;

import util.EventData;

import java.sql.*;

public class SqlWriter {
    private EventData data;
    private Connection connection;
    public static int Veranstaltungsnummer = 1;

    public SqlWriter(EventData data, Connection connection) {
        this.data = data;
        this.connection = connection;
    }

    public void commit(){
        uploadModule();
    }

    private int getVeranstaltungsnummer() {
        return 0;
    }

    public void uploadVeranstaltung() {
        String[][] basicData = this.data.getBasicData();
        int organisationseinheitIndex = this.getIndex("Organisationseinheit", basicData);
        int verantwortlicherIndex = this.getIndex("Verantwortliche/-r", basicData);
        int titelIndex = this.getIndex("Titel", basicData);

        String sqlQuery = "INSERT INTO Veranstaltungen" +
                "(verantwortlicher, organisationseinheit, titel)" +
                "VALUES ('";
        sqlQuery += this.data.getBasicData()[1][verantwortlicherIndex] + "', '"
                + this.data.getBasicData()[1][organisationseinheitIndex] + "', '"
                + this.data.getBasicData()[1][titelIndex] + "');";
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
        //TODO: SQL Anfrage stellen um die event_Ids zu ermitteln

        String sqlQuery = "INSERT INTO Modulzuteilung (modulnummer, veranstaltungsnummer) VALUES ";
        //TODO: Für jedes Modul aus der Modul Tabelle und jedes Event aus der Event Tabelle ein Tupel einfügen.
        for(int i = 1; i < modulTable.length; i++) {
            sqlQuery += "(" + modulTable[i][modulnummerIndex] + ", " + eventId + ")";
            if(i != modulTable.length - 1) {
                sqlQuery += ", ";
            }
        }
        this.upload(sqlQuery);
    }

    public void upload(String sqlQuery) {
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
}