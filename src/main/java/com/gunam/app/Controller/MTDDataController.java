package com.gunam.app.Controller;

import com.gunam.app.Entity.MTDData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSetMetaData;
import java.util.*;

@RestController
@RequestMapping("/api/mtd")
public class MTDDataController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //http://localhost:8080/api/mtd/date?date=01%2F01%2F2016
    @GetMapping("/date")
    public List<MTDData> getDataByDate(@RequestParam("date") String date) {
        String sql = "SELECT * FROM mtd_table WHERE date = ?";
        Object[] params = {date};

        List<MTDData> entries = jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> {
                    MTDData entry = new MTDData();
                    entry.setDate(rs.getString("date"));
                    entry.setTime(rs.getString("time"));

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = rs.getString(columnName);
                        if (columnValue.equals("Default")) {
                            continue;
                        }
                        entry.addColumn(columnName, columnValue);
                    }

                    return entry;
                }
        );

        System.out.println("--Get Data By Date--");
        for (MTDData data : entries)
            System.out.println("Date: " + data.getDate() + " Time: " + data.getTime());

        return entries;
    }

    //http://localhost:8080/api/mtd/date/time?date=01%2F01%2F2016&time=12%3A00%3A00
    @GetMapping("/date/time")
    public MTDData getDataByDateAndTime(@RequestParam("date") String date, @RequestParam("time") String time) {
        String sql = "SELECT * FROM mtd_table WHERE date = ? AND time = ?";
        Object[] params = {date, time};

        MTDData entry = jdbcTemplate.queryForObject(
                sql,
                params,
                (rs, rowNum) -> {
                    MTDData mtdData = new MTDData();
                    mtdData.setDate(rs.getString("date"));
                    mtdData.setTime(rs.getString("time"));

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = rs.getString(columnName);
                        mtdData.addColumn(columnName, columnValue);
                    }

                    return mtdData;
                }
        );

        if (entry == null) {
            throw new NoSuchElementException("Data not found for the specified date and time.");
        }

        System.out.println("--Get Data By Date And Time--");
        System.out.println("Date: " + entry.getDate() + " Time: " + entry.getTime());

        return entry;
    }

    //http://localhost:8080/api/mtd/dateInt?startDate=01%2F01%2F2016&endDate=01%2F02%2F2016
    @GetMapping("/dateInt")
    public List<MTDData> getDataByDateInterval(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        String sql = "SELECT * FROM mtd_table WHERE date BETWEEN ? AND ? ORDER BY date";
        Object[] params = {startDate, endDate};

        List<MTDData> entries = jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> {
                    MTDData entry = new MTDData();
                    entry.setDate(rs.getString("date"));
                    entry.setTime(rs.getString("time"));

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = rs.getString(columnName);
                        entry.addColumn(columnName, columnValue);
                    }

                    return entry;
                }
        );

        System.out.println("--Get Data By Date Interval--");
        System.out.println("Dates Between [" + entries.get(0).getDate() + " , " + entries.get(entries.size()-1).getDate() + "]");
        for (MTDData data : entries)
            System.out.println("Date: " + data.getDate());

        return entries;
    }

    //http://localhost:8080/api/mtd/date/timeInt?date=01%2F01%2F2016&startTime=09%3A00%3A00&endTime=12%3A00%3A00
    @GetMapping("/date/timeInt")
    public List<MTDData> getDataByDateAndTimeInterval(@RequestParam("date") String date, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
        String sql = "SELECT * FROM mtd_table WHERE date = ? AND time BETWEEN ? AND ?";
        Object[] params = {date, startTime, endTime};

        List<MTDData> entries = jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> {
                    MTDData entry = new MTDData();
                    entry.setDate(rs.getString("date"));
                    entry.setTime(rs.getString("time"));

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = rs.getString(columnName);
                        entry.addColumn(columnName, columnValue);
                    }

                    return entry;
                }
        );

        System.out.println("--Get Data By Date And Time Interval--");
        System.out.println("Date: " + entries.get(0).getDate());
        System.out.println("Times Between [" + entries.get(0).getTime() + " , " + entries.get(entries.size()-1).getTime() + "]");
        for (MTDData data : entries)
            System.out.println("Time: " + data.getTime());

        return entries;
    }

    //http://localhost:8080/api/mtd/date/time/column?date=01%2F01%2F2016&time=09%3A00%3A00&column=temp11
    @GetMapping("/date/time/column")
    public String getColumnValueByDateAndTime(@RequestParam("date") String date, @RequestParam("time") String time, @RequestParam("column") String columnName) {
        String sql = "SELECT " + columnName + " FROM mtd_table WHERE date = ? AND time = ?";
        Object[] params = {date, time};

        try {
            String columnValue = jdbcTemplate.queryForObject(sql, params, String.class);
            return columnValue;
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Data not found for the specified date and time.");
        }
    }

    //http://localhost:8080/api/mtd/columnsDate/columnsTime?date=01%2F01%2F2016&time=09%3A00%3A00&columnNames=aux1,aux2,aux3
    @GetMapping("/columnsDate/columnsTime")
    public Map<String, String> getColumnValuesByDateAndTime(@RequestParam("date") String date, @RequestParam("time") String time, @RequestParam("columnNames") List<String> columnNames) {
        String sql = "SELECT " + String.join(", ", columnNames) + " FROM mtd_table WHERE date = ? AND time = ?";
        Object[] params = {date, time};

        try {
            List<Map<String, String>> resultList = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
                Map<String, String> columnValues = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = rs.getString(columnName);
                    columnValues.put(columnName, columnValue);
                }

                return columnValues;
            });

            if (resultList.isEmpty()) {
                throw new NoSuchElementException("Data not found for the specified date and time.");
            }

            return resultList.get(0); // Assuming we only get one row (single result) from the query
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Data not found for the specified date and time.");
        }
    }

    //http://localhost:8080/api/mtd/columnDate/columnTimeInt?date=01%2F01%2F2016&startTime=09%3A00%3A00&endTime=12%3A00%3A00&columnName=aux1
    @GetMapping("/columnDate/columnTimeInt")
    public Map<String, String> getColumnValueInTimeInterval(@RequestParam("date") String date,
                                                            @RequestParam("startTime") String startTime,
                                                            @RequestParam("endTime") String endTime,
                                                            @RequestParam String columnName) {
        String sql = "SELECT time, " + columnName + " FROM mtd_table WHERE date = ? AND time >= ? AND time <= ?";
        Object[] params = {date, startTime, endTime};

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);
        Map<String, String> columnValuesMap = new LinkedHashMap<>();

        for (Map<String, Object> result : results) {
            String time = (String) result.get("time");
            String columnValue = (String) result.get(columnName);
            columnValuesMap.put(time, columnValue);
        }

        System.out.println(date + " " + startTime + " " + endTime + " " + columnName);
        System.out.println(columnValuesMap);

        if (columnValuesMap.isEmpty()) {
            throw new NoSuchElementException("No data found for the specified date and time interval.");
        }

        return columnValuesMap;
    }


    //http://localhost:8080/api/mtd/columnsDate/columnsTimeInt?date=01%2F01%2F2016&startTime=09%3A00%3A00&endTime=12%3A00%3A00&columnNames=aux1,aux2,aux3
    @GetMapping("/columnsDate/columnsTimeInt")
    public List<Map<String, String>> getColumnValuesInTimeInterval(@RequestParam("date") String date, @RequestParam("startTime") String startTime,
                                                                   @RequestParam("endTime") String endTime, @RequestParam List<String> columnNames) {
        String sql = "SELECT " + String.join(", ", columnNames) + " FROM mtd_table WHERE date = ? AND time >= ? AND time <= ?";
        Object[] params = {date, startTime, endTime};

        try {
            List<Map<String, String>> resultList = jdbcTemplate.query(sql, params, (rs) -> {
                List<Map<String, String>> rows = new ArrayList<>();
                while (rs.next()) {
                    Map<String, String> columnValues = new HashMap<>();
                    for (String columnName : columnNames) {
                        columnValues.put(columnName, rs.getString(columnName));
                    }
                    rows.add(columnValues);
                }
                return rows;
            });

            if (resultList.isEmpty()) {
                throw new NoSuchElementException("No data found for the specified date and time interval.");
            }

            return resultList;
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("No data found for the specified date and time interval.");
        }
    }

    @GetMapping("/columnDateInt/columnTimeInt")
    public String getColumnValueInDateAndTimeInterval(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                                      @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
                                                      @RequestParam String columnName) {
        String sql = "SELECT " + columnName + " FROM mtd_table WHERE date >= ? AND date <= ? AND time >= ? AND time <= ?";
        Object[] params = {startDate, endDate, startTime, endTime};

        try {
            String columnValue = jdbcTemplate.queryForObject(sql, params, String.class);

            if (columnValue == null) {
                throw new NoSuchElementException("No data found for the specified date and time interval.");
            }

            return columnValue;
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("No data found for the specified date and time interval.");
        }
    }

    @GetMapping("/columnsDateInt/columnsTimeInt")
    public List<Map<String, String>> getColumnValuesInDateTimeInterval(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                                                       @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
                                                                       @RequestParam String columnName) {
        String sql = "SELECT " + columnName + " FROM mtd_table WHERE date >= ? AND time >= ? AND date <= ? AND time <= ?";
        Object[] params = {startDate, startTime, endDate, endTime};

        try {
            List<Map<String, String>> resultList = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
                Map<String, String> columnValues = new HashMap<>();
                columnValues.put(columnName, rs.getString(columnName));
                return columnValues;
            });

            if (resultList.isEmpty()) {
                throw new NoSuchElementException("No data found for the specified date and time interval.");
            }

            return resultList;
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("No data found for the specified date and time interval.");
        }
    }



}
