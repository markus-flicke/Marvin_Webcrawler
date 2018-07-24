package crawler;

import Exceptions.UnreadableException;
import sql.SqlConnector;
import sql.SqlWriter;
import util.EventData;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class MarvinCrawler {
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        PageNavigator navigator = new PageNavigator();

        navigator.openMarvinSearch();
        System.out.println("Search Opened.");
        navigator.startEmptySearch();
        SqlConnector connector = new SqlConnector();
        Connection connection = connector.connect();
        for(int pageNr = 4; pageNr < 50; pageNr++){
            navigator.goToPage(pageNr);
            System.out.println("Went to Search page Nr. " + pageNr);
            for(int i = 0; i < 10; i++) {
                navigator.openEvent(navigator.getEvent(i));
                EventReader eventReader = new EventReader(navigator);
                try{
                    EventData eventData = eventReader.getEventData();
                    SqlWriter sqlWriter = new SqlWriter(eventData, connection);
                    sqlWriter.uploadAll();
                }
                catch(SQLException e){
                    connection = connector.connect();
                    SqlWriter sqlWriter = new SqlWriter(null, connection);
                    sqlWriter.uploadUnhandled(eventReader.getPermalink(), "SQLException");
                }
                catch(UnreadableException e){
                    connection = connector.connect();
                    SqlWriter sqlWriter = new SqlWriter(null, connection);
                    sqlWriter.uploadUnhandled(eventReader.getPermalink(), "Event unreadable");
                }
                catch(Exception e){
                    connection = connector.connect();
                    SqlWriter sqlWriter = new SqlWriter(null, connection);
                    sqlWriter.uploadUnhandled(eventReader.getPermalink(), "Unexpected Exception");
                }
                finally{
                    navigator.eventBack();
                }
                if(i % 10 == 0){
                    System.out.printf("    Finished event Nr %d\n", i);
                }
            }
        }
        connector.close();
    }

}