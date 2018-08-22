package crawler;

import java.util.logging.Level;

public class Controler {

    static final int PARALELL_CRAWLERS = 2;

    public static void main(String[] args) {
        int pages = getPages(100);
        int pagesPerThread = (int)(pages /PARALELL_CRAWLERS);
        if (pages % PARALELL_CRAWLERS != 0) {
            pagesPerThread += 1;
        }
        int startPage = 1;
        int endPage;

        for(int i = 0; i < PARALELL_CRAWLERS; i++) {
            endPage = startPage + pagesPerThread;
            if (endPage > pages + 1) {
                endPage = pages + 1;
            }
            Thread t = new Thread(new MarvinCrawler(startPage, endPage), "Crawler"+(i+1));
            t.setPriority(t.MAX_PRIORITY);
            t.start();
            System.out.println("Crawler " + (i+1) + " reads pages " + startPage + "-" + (endPage - 1)+ ". Priority: " + t.getPriority());
            startPage = endPage;
        }
    }

    private static int getPages() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        PageNavigator pn = new PageNavigator();
        pn.init();
        int pages = pn.getMaxPage();
        pn.close();
        return pages;
    }
    //For Testing ONLY!:
    private static int getPages(int n) {
        return n;
    }
}

