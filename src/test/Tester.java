package test;

import crawler.EventReader;
import crawler.PageNavigator;
import sql.SqlConnector;
import sql.SqlWriter;
import util.EventData;

public class Tester {
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

    private static void sqlWriterTest(){
        PageNavigator pn = new PageNavigator();
        pn.get("https://marvin.uni-marburg.de:443/qisserver/pages/startFlow.xhtml?_flowId=showEvent-flow&unitId=10531&" +
                "termYear=2018&termTypeValueId=30&navigationPosition=studiesOffered,searchCourses");//AuD Übungen
        EventReader er = new EventReader(pn);
        EventData eventData = er.getEventData();
        SqlConnector connector = new SqlConnector();
        try{
            SqlWriter sqlWriter = new SqlWriter(eventData, connector.connect());
            sqlWriter.commit();
        }
        finally{
            connector.close();
        }
    }

    public static void main(String[] args){
        sqlWriterTest();
    }
}
