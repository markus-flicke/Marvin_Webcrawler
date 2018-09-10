package CalendarGeneration;

import sql.SqlConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLReader {
    public static List<CalendarEntry> getEvents(List<String> titles) throws SQLException{
        List<CalendarEntry> res = new ArrayList<>();
        for(String title: titles){
            res.addAll(getEvents(title));
        }
        return res;
    }

    public static List<CalendarEntry> getEvents(String title) throws SQLException{
        Connection connection = new SqlConnector().getConnection();
        ResultSet resultSet = query("Select distinct titel, verantwortlicher, wochentag, von, bis, raum, startdatum, enddatum from veranstaltungen " +
                "natural join events " +
                "where titel like '%" + title + "%' and not titel like '%Übung%' and rhythmus = 'wöchentlich'", connection);
        List<CalendarEntry> res= new ArrayList<>();
        while(resultSet.next()){
            res.add( new CalendarEntry(resultSet));
        }
        return res;
    }

    private static ResultSet query(String sqlQuery, Connection connection){
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

    public static void main(String[] args){
        try {
            List<String> input = new ArrayList<>();
            input.add("Theoretische Info");
            input.add("Logik");
            input.add("Softwaretechnik");
            input.add("Grundlagen der Statistik");

            ConsoleCalendar consoleCalendar = new ConsoleCalendar(getEvents(input));
            System.out.print(consoleCalendar.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
