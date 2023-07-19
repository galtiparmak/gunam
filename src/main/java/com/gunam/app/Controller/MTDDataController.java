package com.gunam.app.Controller;

import com.gunam.app.Entity.MTDData;
import com.gunam.app.Repository.MTDDataEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/mtd")
public class MTDDataController {
    private MTDDataEntryRepository dataEntryRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/{date}")
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


    @GetMapping("/{date}/{time}")
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


}
