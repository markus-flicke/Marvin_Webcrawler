package crawler;

import Exceptions.UnreadableException;
import sql.SqlConnector;
import sql.SqlWriter;
import util.EventData;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class MarvinCrawler implements Runnable{
    int currentPage, currentEvent, end;
    final int ENTRIES_PER_PAGE = 10;

    public MarvinCrawler(int start, int end) {
        this.currentPage = start;
        this.end = end;
    }

    @Override
    public void run() {
        this.loopCrawl();
    }

    public void loopCrawl(){
        //currentPage = 20;
        currentEvent = 0;
        while(true){
            try{
                crawl(currentPage, currentEvent,end);
                break;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("There are only " + currentEvent + " entries on page " + currentPage + ".");
                break;
            } catch(Exception e){
                System.out.println("Some sort of exception terminated the crawler. restarting...");
                System.out.printf("CurrentPage: %d\nCurrentEvent: %d\n", currentPage, currentEvent);
                //System.out.println(e);
                //e.printStackTrace();
            }
        }
    }

    private void crawl(int startPage, int startEvent, int endPage){
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        PageNavigator navigator = new PageNavigator();
        navigator.init();
        System.out.println(this.toString() + ": Search Opened.");
        SqlConnector connector = new SqlConnector();
        Connection connection = connector.connect();

        int eventOffset = startEvent;
        for(int pageNr = startPage; pageNr < endPage; pageNr++){ //Exlusive endpage!

            currentPage = pageNr;
            navigator.goToPage(pageNr);
            System.out.println(this.toString() + ": Went to Search page Nr. " + pageNr);
            for(int i = 0; i < ENTRIES_PER_PAGE; i++) {
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
            }

        }
        connector.close();
    }
}