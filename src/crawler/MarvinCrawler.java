package crawler;

import sql.SqlConnector;
import sql.SqlWriter;
import util.EventData;
import java.sql.Connection;
import java.util.logging.Level;

public class MarvinCrawler {
    /**
     * The relative Path to the Geckodriver for Firefox automation
     * @see <a href="https://github.com/mozilla/geckodriver">https://github.com/mozilla/geckodriver</a>
     */
    private static final String GECKODRIVER_PATH = "./lib/firefoxdriver/geckodriver";    //For Firefox driver
    private static final int ENTRIES_PER_PAGE = 5;
    private static int NUMBER_OF_PAGES = 1; //TODO: If possible use PageReader.getMaxPages() to set NUMBER_OF_PAGES to the correct value.

    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        PageNavigator navigator = new PageNavigator();

        navigator.openMarvinSearch();
        System.out.println("Search Opened.");
        navigator.startEmptySearch();
        int entries = 10;
        navigator.setEntriesPerPage(entries);
        System.out.println("Entries per page set.");
        navigator.goToPage(2);
        System.out.println("Went to Search page Nr. x");
        SqlConnector connector = new SqlConnector();
        Connection connection = connector.connect();
        for(int i = 0; i < entries; i++) {
            navigator.openEvent(navigator.getEvent(i));
            EventReader eventReader = new EventReader(navigator);
            try{
                EventData eventData = eventReader.getEventData();
                SqlWriter sqlWriter = new SqlWriter(eventData, connection);
                sqlWriter.uploadAll();
            }
            catch(Exception e){
                SqlWriter sqlWriter = new SqlWriter(null, connection);
                sqlWriter.upload(String.format("INSERT INTO unhandled values ('%s')",eventReader.getPermalink()));
            }
            finally{
                navigator.eventBack();
            }
            if(i % 10 == 0){
                System.out.printf("Finished event Nr %d\n", i);
            }
        }
    }
}