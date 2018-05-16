package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import marvin.Table;

import java.util.Iterator;
import java.util.List;
import org.openqa.selenium.NoSuchElementException;

public class EventReader extends FirefoxDriver implements Runnable{

    private WebElement event;
    private static int shortWait = 100;

    public EventReader(WebElement event) {
        //super(true);
        this.event = event;
    }

    @Override
    public void run() {
        getEventData();
        this.close();
    }

    public Table getGrundDaten() {
        String lableHeaderClass = "labelWithBG";
        String divValueClass = "answer";

        //TODO: implement Methode to avoid redundancy in Get Headers and Get Values
        //Get Headers:
        List<WebElement> headersList = this.findElements(By.className(lableHeaderClass));
        String[] headers = new String[headersList.size()];
        // TODO: Check runntime: List Iteration vs List.get(index)
        Iterator<WebElement> it = headersList.iterator();
        for(int i = 0; i < headers.length; i++) {
            headers[i] = it.next().getText();
        }

        //Initialise Table
        Table grundDaten = new Table(headers);

        //Get Values
        List<WebElement> valuesList= this.findElements(By.className(divValueClass));
        String[] values = new String[valuesList.size()];
        it = valuesList.iterator();
        for(int i = 0; i < values.length; i++) {
            values[i] = it.next().getText();
        }

        //Add Values to Table
        grundDaten.add(values);

        return grundDaten;
    }

    private void openEvent(WebElement link) {
        String buttonBackButtonID = "showEvent:backButtonTop";

        link.click();
        while(true) {   //TODO: replace this with method to avoid redundancy (like in goBackToEventsPage or every other waiting event.
            try {
                this.findElement(By.id(buttonBackButtonID));
                return;  //--> Page is opened
            } catch (NoSuchElementException e) {
                this.pause(shortWait);
                System.out.println("Open Event wait");
            }
        }
    }

    private void goBackToEventsPage() {
        String buttonNewSearchID = "genSearchRes:buttonsTop:newSearch";
        String buttonBackButtonID = "showEvent:backButtonTop";

        WebElement buttonBackButton = this.findElement(By.id(buttonBackButtonID));
        buttonBackButton.click();

        while(true) {   //TODO replace with methode like in openEvent()
            try {
                this.findElement(By.id(buttonNewSearchID));
            } catch (NoSuchElementException e) {
                this.pause(shortWait);
                System.out.println("Go Back wait");
            }
        }
    }

    public void getEventData() {
        openEvent(event);
        getGrundDaten().print();
    }

    public void pause(long timeout) {
        synchronized (this) {
            try {
                this.wait(timeout); //wait has to be in synchronised block to work
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
