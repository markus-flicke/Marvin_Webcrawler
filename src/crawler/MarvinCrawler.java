package crawler;

import Exceptions.UnreadableException;
import sql.SqlConnector;
import sql.SqlWriter;
import util.EventData;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class MarvinCrawler {
    int currentPage, currentEvent;

    public void loopCrawl(){
        currentPage = 201;
        currentEvent = 0;
        while(currentPage <= 202){  //TODO: method for max page
            try{
                crawl(currentPage, currentEvent,202);
            }catch(Exception e){
                System.out.println("Some sort of exception terminated the crawler. restarting...");
                System.out.printf("CurrentPage: %d\nCurrentEvent: %d\n", currentPage, currentEvent);
                System.out.println(e);
            }
        }
    }

    private void crawl(int startPage, int startEvent, int endPage){
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        PageNavigator navigator = new PageNavigator();

        navigator.openMarvinSearch();
        System.out.println("Search Opened.");
        navigator.startEmptySearch();
        SqlConnector connector = new SqlConnector();
        Connection connection = connector.connect();

        int eventOffset = startEvent;
        for(int pageNr = startPage; pageNr <= endPage; pageNr++){

            currentPage = pageNr;
            navigator.goToPage(pageNr);
            System.out.println("Went to Search page Nr. " + pageNr);
            for(int i = 0; i < 10; i++) {
                currentEvent = i;
                i += eventOffset;
                eventOffset = 0;
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

    public static void main(String[] args) {
        MarvinCrawler marvinCrawler = new MarvinCrawler();
        marvinCrawler.loopCrawl();
    }
}