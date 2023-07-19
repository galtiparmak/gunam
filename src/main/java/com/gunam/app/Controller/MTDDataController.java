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

    @GetMapping("/date/{date}")
    public List<MTDData> getDataByDate(@PathVariable String date) {
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


    @GetMapping("/date/{date}/time/{time}")
    public MTDData getDataByDateAndTime(@PathVariable String date, @PathVariable String time) {
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

    @GetMapping("/dateInt/{startDate}/{endDate}")
    public List<MTDData> getDataByDateInterval(@PathVariable String startDate, @PathVariable String endDate) {
        String sql = "SELECT * FROM mtd_table WHERE date BETWEEN ? AND ?";
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

    @GetMapping("/date/{date}/timeInt/{startTime}/{endTime}")
    public List<MTDData> getDataByDateAndTimeInterval(@PathVariable String date, @PathVariable String startTime, @PathVariable String endTime) {
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

    @GetMapping("/date/{date}/time/{time}/column/{columnName}")
    public String getColumnValueByDateAndTime(@PathVariable String date, @PathVariable String time, @PathVariable String columnName) {
        String sql = "SELECT " + columnName + " FROM mtd_table WHERE date = ? AND time = ?";
        Object[] params = {date, time};

        try {
            String columnValue = jdbcTemplate.queryForObject(sql, params, String.class);
            return columnValue;
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Data not found for the specified date and time.");
        }
    }


    // USAGE: GET /api/data/{date}/{time}?columnNames=column1,column2,column3
    @GetMapping("/columnsDate/{date}/columnsTime/{time}")
    public Map<String, String> getColumnValuesByDateAndTime(@PathVariable String date, @PathVariable String time, @RequestParam("columnNames") List<String> columnNames) {
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

    @GetMapping("/columnDate/{date}/columnTimeInt/{startTime}/{endTime}")
    public String getColumnValueInTimeInterval(@PathVariable String date, @PathVariable String startTime,
                                               @PathVariable String endTime, @RequestParam String columnName) {
        String sql = "SELECT " + columnName + " FROM mtd_table WHERE date = ? AND time >= ? AND time <= ?";
        Object[] params = {date, startTime, endTime};

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

    @GetMapping("/columnsDate/{date}/columnsTimeInt{startTime}/{endTime}")
    public List<Map<String, String>> getColumnValuesInTimeInterval(@PathVariable String date, @PathVariable String startTime,
                                                                   @PathVariable String endTime, @RequestParam List<String> columnNames) {
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

    @GetMapping("/columnDateInt/{startDate}/{endDate}/columnTimeInt/{startTime}/{endTime}")
    public String getColumnValueInDateAndTimeInterval(@PathVariable String startDate, @PathVariable String endDate,
                                                      @PathVariable String startTime, @PathVariable String endTime,
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

    @GetMapping("/columnsDateInt/{startDate}/{endDate}/columnsTimeInt{startTime}/{endTime}")
    public List<Map<String, String>> getColumnValuesInDateTimeInterval(@PathVariable String startDate, @PathVariable String endDate,
                                                                       @PathVariable String startTime, @PathVariable String endTime,
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
