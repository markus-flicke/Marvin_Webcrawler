package crawler;

public class MarvinCrawler {
    /**
     * The relative Path to the Geckodriver for Firefox automation
     * @see <a href="https://github.com/mozilla/geckodriver">https://github.com/mozilla/geckodriver</a>
     */
    private static final String GECKODRIVER_PATH = "./lib/firefoxdriver/geckodriver";    //For Firefox driver
    private static final int ENTRIES_PER_PAGE = 30;
    private static int NUMBER_OF_PAGES = 1; //TODO: If possible use PageReader.getMaxPages() to set NUMBER_OF_PAGES to the correct value.

    public static void main(String[] args) {
        for(int i = 0; i < NUMBER_OF_PAGES; i++) {
            new Thread(new PageReader(i + 1, ENTRIES_PER_PAGE)).start();
        }
    }
}
