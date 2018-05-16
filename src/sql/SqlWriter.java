package sql;

import util.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class SqlWriter {
    private final static String user = "postgres", password = "somthing", url = "jdbc:postgresql://localhost/Vorlesungsverzeichnis";
    public static void main(String args[]) {
        Connection connection = connect(user, password, "localhost", "Vorlesungsverzeichnis");
        createEventsTable(connection);

        try {
            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection connect(String username, String password, String host, String database){
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://"+ host + "/"+ database,username, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return c;
    }

    private static void createEventsTable(Connection connection) { ;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            String sql = "CREATE TABLE EVENTS " +
                    "(Titel varchar(300), " +
                    "bemerkung varchar(200), " +
                    "verantwortlicher varchar(1000), " +
                    "raum varchar(400), " +
                    "wochentag varchar(60), " +
                    "von varchar(60) , " +
                    "bis varchar(60), " +
                    "rhythmus varchar(60), " +
                    "startdatum varchar(60), " +
                    "enddatum varchar(60), " +
                    "organisationseinheit varchar(300) , " +
                    "permalink varchar(400))";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table 'EVENTS' created successfully");
    }

    private static void insertEvent(Table t) {

    }

    //TODO implement... maybe in other Classes to parallelize this porcess if possible.
    private void insertModul() {

    }
}
