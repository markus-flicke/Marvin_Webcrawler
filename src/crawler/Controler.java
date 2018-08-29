package crawler;

import java.util.logging.Level;

public class Controler {

    static int PARALELL_CRAWLERS = 4;
    static int CURRENT_TERM = 76;

    public static void main(String[] args) {
        if(args.length > 0) {
            int tmp = Integer.parseInt(args[0]);
            if(tmp <= 8) {
                PARALELL_CRAWLERS = tmp;
            }
        }
        System.out.println(PARALELL_CRAWLERS + " Crawlers starting. . .");
        int pages = getPages();
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
            Thread t = new Thread(new MarvinCrawler(startPage, endPage, CURRENT_TERM), "Crawler"+(i+1));
            t.setPriority(t.MAX_PRIORITY);
            t.start();
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

