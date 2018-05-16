package util;

import java.util.ArrayList;

public class Table {
    private ArrayList<String[]> rows;
    private String[] headers;

    public Table(int size) {
        headers = new String[size];
        rows = new ArrayList<>();
    }
    public Table(String[] headers) {
        this.headers = new String[headers.length];
        for(int i = 0; i < this.headers.length; i++) {
            this.headers[i] = headers[i];
        }
        rows = new ArrayList<>();
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public void add(String[] row) throws IndexOutOfBoundsException{
        if(row.length == headers.length) {
            rows.add(row);
        } else {
            throw new IndexOutOfBoundsException("Row size doesn't fit Table size!");
        }
    }

    public void print() {
        for(int i = 0; i < headers.length; i++) {
            System.out.print(headers[i] + ": ");
            for(int j = 0; j < rows.size(); j++) {
                String[] row = rows.get(j);
                System.out.println(row[i]);
            }
            System.out.println();
        }
    }

    public String getSqlInsertString(String sqlTable) {
        String s =  "INSERT INTO " + sqlTable + " (";
        for(int i = 0; i < headers.length - 1; i++) {
            s += headers[i] + ", ";
        }
        s += headers[headers.length - 1] + ")\n";
        s += "VALUES ";
        for(int i = 0; i < rows.size(); i++) {
            s += "(";
            String [] row = rows.get(i);
            for(int j = 0; j < row.length - 1; j++) {
                s += row[j] + ", ";
            }
            s += row[row.length - 1] + ")\n";
        }
        return s + ";";
    }   //Das Ergebnis ist noch nicht getestet und sicher nicht korrekt.

}
