package Exceptions;

import crawler.EventReader;
import crawler.PageNavigator;
import sql.SqlConnector;
import sql.SqlWriter;
import util.EventData;

import java.sql.SQLException;

public class Tester {
    public static void test(String url) throws UnreadableException{
        PageNavigator pn = new PageNavigator();
        pn.get(url);
        pn.startEmptySearch();
        pn.goToPage(25);
        pn.openEvent(pn.getEvent(1));
        System.out.println(pn.getTitle());
    }
    public static void main(String[] args){
        try{
            test("https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourseData.xhtml?_" +
                    "flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1");
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
