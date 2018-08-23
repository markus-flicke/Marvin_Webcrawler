package crawler;

import Exceptions.UnreadableException;
import sql.SqlConnector;
import sql.SqlWriter;
import util.EventData;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class MarvinCrawler implements Runnable{
    int currentPage, currentEvent, end;
    final int ENTRIES_PER_PAGE = 10;

    public MarvinCrawler(int start, int end) {
        System.setProperty("webdriver.gecko.driver", "/home/jakob/Schreibtisch/Fortgeschrittenen_Praktikum/Marvin_Webcrawler/lib/firefoxdriver/geckodriver");
        this.currentPage = start;
        this.end = end;
        System.out.println(this.toString() + " is going to read pages " + start + " - " + (end - 1) + ".");
    }

    @Override
    public void run() {
        System.out.println(this.toString() + " started at: " + getDate());
        this.loopCrawl();
        System.out.println(this.toString() + " finished at: " + getDate());
    }
    private String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public void loopCrawl(){
        //currentPage = 20;
        currentEvent = 0;
        while(true){
            try {
                crawl(currentPage, currentEvent, end);
                break;
            } catch(Exception e){
                System.out.println("Some sort of exception terminated the crawler. restarting...");
                System.out.printf("CurrentPage: %d\tCurrentEvent: %d\n", currentPage, currentEvent);
                //System.out.println(e);
                e.printStackTrace();
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
            try {navigator.goToPage(pageNr);} catch (Exception e) {pageNr--; continue; }//retry
            System.out.println(this.toString() + ": Went to Search page Nr. " + pageNr);
            //System.out.println(navigator.getTitle());
            for(int i = 0; i < ENTRIES_PER_PAGE; i++) {
                currentEvent = i;
                i += eventOffset;
                eventOffset = 0;
                try {navigator.openEvent(i);} catch (Exception e) {System.out.println(this.getClass() + " ended on page " + currentPage + " event " + (currentEvent - 1));break;}
                EventReader eventReader = new EventReader(navigator);
                try{
                    EventData eventData = eventReader.getEventData();
                    SqlWriter sqlWriter = new SqlWriter(eventData, connection);
                    sqlWriter.uploadAll();
                }
                catch(Exception e){
                    connection = connector.connect();
                    SqlWriter sqlWriter = new SqlWriter(null, connection);
                    sqlWriter.uploadUnhandled(eventReader.getPermalink(), ""+e.getClass()+ " -> " + e.getMessage());
                }
                finally{
                    navigator.eventBack();
                }
            }

        }
        connector.close();
    }
}