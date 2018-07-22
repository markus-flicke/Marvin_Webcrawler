//package test;
//
//import crawler.EventReader;
//import crawler.PageNavigator;
//import sql.SqlConnector;
//import sql.SqlWriter;
//import util.EventData;
//
//import java.util.logging.Level;
//
//public class Tester {
//    private static void testAuD() {
//        PageNavigator pn = new PageNavigator();
//        pn.get("https://marvin.uni-marburg.de:443/qisserver/pages/startFlow.xhtml?_flowId=showEvent-flow&unitId=10531&" +
//                "termYear=2018&termTypeValueId=30&navigationPosition=studiesOffered,searchCourses");//AuD Übungen
//        EventReader er = new EventReader(pn);
//        System.out.println(er.getEventData().toString());
//    }
//
//    private static void testDekla() {
//        PageNavigator pn = new PageNavigator();
//        pn.get("https://marvin.uni-marburg.de:443/qisserver/pages/startFlow.xhtml?_flowId=showEvent-flow&unitId=" +
//                "16751&termYear=2018&termTypeValueId=30&navigationPosition=studiesOffered,searchCourses");//Dekla
//        EventReader er = new EventReader(pn);
//        System.out.println(er.getEventData().toString());
//    }
//
//    private static void sqlWriterTest(){
//        System.out.println("SQLWriter Test started:");
//        PageNavigator pn = new PageNavigator();
//        pn.get("https://marvin.uni-marburg.de:443/qisserver/pages/startFlow.xhtml?_flowId=showEvent-flow&unitId=10531&" +
//                "termYear=2018&termTypeValueId=30&navigationPosition=studiesOffered,searchCourses");
//        EventReader er = new EventReader(pn);
//        EventData eventData = er.getEventData();
//        SqlConnector connector = new SqlConnector();
//
//        SqlWriter sqlWriter = new SqlWriter(eventData, connector.connect());
//        sqlWriter.uploadAll();
//
//
//        System.out.println("test successful");
//    }
//
//    public static void main(String[] args){
//        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
//        sqlWriterTest();
//    }
//
//}
