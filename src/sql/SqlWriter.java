package sql;

import util.EventData;

import java.sql.*;

//TODO: TEST and Refactoring
public class SqlWriter {
    private final static String user = "postgres", password = "somthing", url = "jdbc:postgresql://localhost/Vorlesungsverzeichnis";
    private EventData data;
    private Connection connection;
    public static int Veranstaltungsnummer = 1;

    public SqlWriter(EventData data) {
        this.data = data;
        this.connection = this.connect(user, password, "localhost", "postgres");
    }

    private Connection connect(String username, String password, String host, String database){
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://"+ host + "/"+ database,username, password);
            c.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return c;
    }

    private int getVeranstaltungsnummer() {
        //get modul numbers from data:
        long[] modulNumbers = new long[data.getModuleTable().length - 1];
        for (int i = 0; i < modulNumbers.length; i++) {
            modulNumbers[i] = Long.parseLong(data.getModuleTable()[i + 1][0]);
        }
        //build Querry String:
        String sqlQuerry = "SELECT veranstaltungsnummer" +
                "FROM Modulzuteilung NATURAL JOIN Module" +
                "WHERE Modulnummer IN (";
        for(int i = 0; i < modulNumbers.length; i++) {
            sqlQuerry += modulNumbers[i] + ((i == modulNumbers.length - 1)?"":", ");
        }
        sqlQuerry += ");";
        //check if they allready exist in Modulzuteilung:
        try {
            Statement stmt = connection.createStatement();
            ResultSet results = stmt.executeQuery(sqlQuerry);
            results.last();
            int count = results.getRow();
            results.beforeFirst();
            if(count == 1) {
                results.next();
                return results.getInt("veranstaltungsnummer");
            } else if(count == 0) {
                return 0;
            } else{
                throw new RuntimeException("There was a severe Error in Table: Modulzuweisung\n Count was: "+ count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void uploadVeranstaltung() {
        String[][] basicData = this.data.getBasicData();
        int veranstaltungsnummer = this.getVeranstaltungsnummer();
        int verantwortlicherIndex = this.getIndex("Verantwortliche/-r", basicData);
        int organisationseinheitIndex = this.getIndex("Organisationseinheit", basicData);
        int titelIndex = this.getIndex("Titel", basicData);

        if(veranstaltungsnummer == 0) {
            String sqlQuerry = "INSERT INTO Veranstaltungen" +
                    "(verantwortlicher, organisationseinheit, titel)" +
                    "VALUES ('";
            sqlQuerry += this.data.getBasicData()[1][verantwortlicherIndex] + "', '"
                    + this.data.getBasicData()[1][organisationseinheitIndex] + "', '"
                    + this.data.getBasicData()[1][titelIndex] + "');";
            this.upload(sqlQuerry);
        }
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
        String sqlQuerry = "INSERT INTO Module "+
                "(modulnummer, modulkuerzel, bezeichnung) "+
                "VALUES ";

        for(int i = 1; i < modulTable.length; i++) {
            sqlQuerry += "("+ modulTable[i][modulnummerIndex] + ", '" +
                    modulTable[i][modulkuerzelIndex] + "', '" +
                    modulTable[i][bezeichnungIndex]+ "')";
            if(i != modulTable.length - 1) {
                sqlQuerry += ", ";
            }

        }
        this.upload(sqlQuerry);
    }

    //!Es müssen erst die Module, die Veranstaltung, dann die Modulzuteilung und dann die events hochgeladen werden
    private void uploadEvents() {
        String[][] eventTable = this.data.getEventTable();
        int veranstaltungsnummer = this.getVeranstaltungsnummer();
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
        String sqlQuerry = "INSERT INTO Events (veranstaltungsnummer, wochentag, von, bis, akademischezeit," +
                "rhythmus, startdatum , enddatum, teilnehmerzahl , raum , durchführender , ausfalltermin , " +
                "bemerkung ) VALUES";

        for(int i = 0; i < eventTable.length; i++) {
            sqlQuerry += "("+ veranstaltungsnummer + ", '" + eventTable[i][wochentagIndex] + "', '" +
                  eventTable[i][vonIndex] + "', '" + eventTable[i][bisIndex] + "', '" +
                    eventTable[i][akademischezeitIndex] + "', '" + eventTable[i][rhythmusIndex] + "', '" +
                    eventTable[i][startDatumIndex] + "', '" + eventTable[i][endDatumIndex] + "', '" +
                    eventTable[i][teilnemherzahlIndex] + "', '" + eventTable[i][raumIndex]  + "', '" +
                    eventTable[i][durchführenderIndex] + "', '" + eventTable[i][ausfallterminIndex] + "', '" +
                    eventTable[i][bemerkungIndex]+"')";
            if(i != eventTable.length - 1) {
                sqlQuerry += ", ";
            }
        }
        this.upload(sqlQuerry);
    }

    private void uploadModulzuteilung() {
        int veranstaltungsnummer = this.getVeranstaltungsnummer();
        String[][] modulTable = this.data.getModuleTable();
        int modulnummerIndex = getIndex("Modulnummer", modulTable);
        String sqlQuerry = "INSERT INTO Modulzuteilung (modulnummer, veranstaltungsnummer) VALUES ";

        for(int i = 1; i < modulTable.length; i++) {
            sqlQuerry += "(" + modulTable[i][modulnummerIndex] + ", " + veranstaltungsnummer + ")";
            if(i != modulTable.length - 1) {
                sqlQuerry += ", ";
            }
        }
        this.upload(sqlQuerry);
    }

    private void upload(String sqlQuerry) {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sqlQuerry);
        } catch (SQLException e) {
            throw new RuntimeException("Upload nicht möglich. Insertstatment:\n" + sqlQuerry +
                    "\nkonnte nicht ausgeführt werden.");
        }
    }
}