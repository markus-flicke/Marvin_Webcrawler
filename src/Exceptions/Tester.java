package Exceptions;

import crawler.EventReader;
import crawler.PageNavigator;
import sql.SqlConnector;
import sql.SqlWriter;
import util.EventData;

import java.sql.SQLException;

public class Tester {
    public static void showError(String url) throws UnreadableException{
        PageNavigator pn = new PageNavigator();
        pn.get(url);
        EventReader er = new EventReader(pn);
        EventData eventData = er.getEventData();
        SqlConnector connector = new SqlConnector();
        SqlWriter sqlWriter = new SqlWriter(eventData, connector.connect());
        try{
            sqlWriter.uploadAll();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        System.out.println("test successful");
    }
    public static void main(String[] args){
        try{
            showError("some url");
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
