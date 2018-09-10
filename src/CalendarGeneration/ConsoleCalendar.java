package CalendarGeneration;

import com.gargoylesoftware.htmlunit.javascript.host.Console;

import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static CalendarGeneration.SQLReader.getEvents;

public class ConsoleCalendar {
    List<CalendarEntry> entries = new ArrayList<>();

    public ConsoleCalendar(){}

    public ConsoleCalendar(List<CalendarEntry> entries) {
        this.entries = entries;
    }

    class Weekday{
        List<CalendarEntry> entries = new ArrayList<>();

        Weekday(String day, List<CalendarEntry> entries){
            for(CalendarEntry e: entries){
                if(e.weekDay.toLowerCase().equals(day.toLowerCase())) {
                    this.entries.add(e);
                    this.entries.sort((t, o) -> valueTime(t.startTime) - valueTime(o.startTime));
                }
            }
        }

        public CalendarEntry dequeue() throws EmptyQueueException{
            if(entries.isEmpty()){
                throw new EmptyQueueException("No more Events for this day!");
            }
            return entries.remove(0);
        }

        public String toString(){
            return entries.toString();
        }

        private int valueTime(String time){
            return Integer.parseInt(time.replace(":", ""));
        }
    }

    class EmptyQueueException extends Exception{
        EmptyQueueException(String m){
            super(m);
        }
    }

    @Override
    public String toString() {
        String res = "";
        res += String.format("|%37s", "Montag");
        res += String.format("|%37s", "Dienstag");
        res += String.format("|%37s", "Mittwoch");
        res += String.format("|%37s", "Donnerstag");
        res += String.format("|%37s|", "Freitag");
        res += "\n";

        List<Weekday> weekdays = new ArrayList<>();
        weekdays.add(new Weekday("Montag", entries));
        weekdays.add(new Weekday("Dienstag", entries));
        weekdays.add(new Weekday("Mittwoch", entries));
        weekdays.add(new Weekday("Donnerstag", entries));
        weekdays.add(new Weekday("Freitag", entries));

        if(!entries.isEmpty()){
            boolean hadEvents = true;

            while(hadEvents){
                hadEvents = false;
                res+= "|";
                for(Weekday weekday: weekdays){
                    CalendarEntry e = null;
                    try {
                        e = weekday.dequeue();
                    } catch (EmptyQueueException e1) {
                        res += String.format("%37s|", " ");
                        continue;
                    }

                    res += String.format("%17s von %s bis %s|",e.title
                            .substring(0,(e.title.length() > 17) ? 17 : e.title.length()),e.startTime, e.endTime);
                    hadEvents = true;
                }
                res += "\n";
            }
        }
        return res;
    }

    public static void main(String[] args){
        List<String> input = new ArrayList<>();
        input.add("Theoretische Info");
        input.add("Logik");
        input.add("Softwaretechnik");
        input.add("Grundlagen der Statistik");

        try {
            System.out.print(new ConsoleCalendar(getEvents(input)));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
