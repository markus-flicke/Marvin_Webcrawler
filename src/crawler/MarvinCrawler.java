package crawler;

import util.EventData;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class MarvinCrawler {
    /**
     * The relative Path to the Geckodriver for Firefox automation
     * @see <a href="https://github.com/mozilla/geckodriver">https://github.com/mozilla/geckodriver</a>
     */
    private static final String GECKODRIVER_PATH = "./lib/firefoxdriver/geckodriver";    //For Firefox driver
    private static final int ENTRIES_PER_PAGE = 5;
    private static int NUMBER_OF_PAGES = 1; //TODO: If possible use PageReader.getMaxPages() to set NUMBER_OF_PAGES to the correct value.

    public static void serializeEventDataForSQLTest(){
        int entriesPerPage = 20;
        int pagenumber = 1;
        PageNavigator pageNavigator = new PageNavigator();
        pageNavigator.openMarvinSearch();
        System.out.println(pageNavigator.getTitle());
        pageNavigator.startEmptySearch();
        System.out.println(pageNavigator.getMaxPage());
        pageNavigator.setEntriesPerPage(entriesPerPage);
        System.out.println(pageNavigator.getMaxPage());
        pageNavigator.goToPage(pagenumber);
        for(int i = 0; i < entriesPerPage; i++) {
            pageNavigator.openEvent(pageNavigator.getEvent(i));
            EventData eventData = new EventReader(pageNavigator).getEventData();
            System.out.println(eventData);

            try {
                FileOutputStream fos = new FileOutputStream("eventData.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(eventData);
                oos.close();
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }

            break;
        }
    }

    public static void main(String[] args) {
        serializeEventDataForSQLTest();
    }
}