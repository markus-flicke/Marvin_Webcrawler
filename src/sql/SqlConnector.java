package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnector {
    private final static String user = "postgres", password = "somthing", connectionString = "jdbc:postgresql://localhost/Vorlesungsverzeichnis";
    private Connection c;

    public Connection connect(){
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(connectionString, user, password);
            c.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return c;
    }

    public void close() {
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
