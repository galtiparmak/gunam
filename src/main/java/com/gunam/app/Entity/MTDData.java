package com.gunam.app.Entity;

import java.util.HashMap;
import java.util.Map;

public class MTDData {
    private String date;
    private String time;
    private Map<String, String> columns;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

    public void addColumn(String columnName, String columnValue) {
        if (columns == null) {
            columns = new HashMap<>();
        }
        columns.put(columnName, columnValue);
    }
}

