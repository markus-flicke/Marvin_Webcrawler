package CalendarGeneration;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CalendarEntry {
    String title;
    String startTime;
    String endTime;
    String location;
    String verantwortlicher;
    String startDate, endDate;
    String weekDay;

    public CalendarEntry(String title, String startTime, String endTime, String location, String verantwortlicher, String startDate, String endDate, String weekDay) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.verantwortlicher = verantwortlicher;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weekDay = weekDay;
    }

    public CalendarEntry(ResultSet resultSet) throws SQLException{
        title = resultSet.getString("titel");
        startTime = resultSet.getString("von");
        endTime = resultSet.getString("bis");
        location = resultSet.getString("raum");
        verantwortlicher = resultSet.getString("verantwortlicher");
        startDate = resultSet.getString("startdatum");
        endDate = resultSet.getString("enddatum");
        weekDay = resultSet.getString("wochentag");
    }

    public String toString(){
        String res = "";
        res += "\ntitel: " + title;
        res += "\nstartTime: " + startTime;
        res += "\nendTime: " + endTime;
        res += "\nlocation: " + location;
        res += "\nverantwortlicher: " + verantwortlicher;
        res += "\nstartDate: " + startDate;
        res += "\nendDate: " + endDate;
        res += "\nweekDay: " + weekDay;
        return res + "\n";
    }
}
