package test;

import crawler.EventReader;
import crawler.PageNavigator;
import org.openqa.selenium.By;

public class Tester {
    public static void main(String[] args) {
        testAuD();
    }

    private static void testAuD() {
        PageNavigator pn = new PageNavigator();
        pn.get("https://marvin.uni-marburg.de:443/qisserver/pages/startFlow.xhtml?_flowId=showEvent-flow&unitId=10531&" +
                "termYear=2018&termTypeValueId=30&navigationPosition=studiesOffered,searchCourses");//AuD Übungen
        EventReader er = new EventReader(pn);
        System.out.println(er.getEventData().toString());
    }
    private static void testDekla() {
        PageNavigator pn = new PageNavigator();
        pn.get("https://marvin.uni-marburg.de:443/qisserver/pages/startFlow.xhtml?_flowId=showEvent-flow&unitId=" +
                "16751&termYear=2018&termTypeValueId=30&navigationPosition=studiesOffered,searchCourses");//Dekla
        EventReader er = new EventReader(pn);
        System.out.println(er.getEventData().toString());
    }
}
