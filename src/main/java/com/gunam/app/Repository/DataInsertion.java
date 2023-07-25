package com.gunam.app.Repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DataInsertion {
    private final JdbcTemplate jdbcTemplate;
    private Set<String> existingColumns;
    private int columnLen;

    public DataInsertion(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.existingColumns = new HashSet<>();
    }

    public void createAndInsertData(String dataString) {
        // Split the data string into rows
        String[] rows = dataString.split("\n");

        // Get the column names from the first row
        String[] columnNames = rows[0].split(",");
        if (!columnNames[0].trim().equalsIgnoreCase("Date")) {
            return;
        }
        columnLen = columnNames.length;

        for (int i = 0; i < columnLen; i++) {
            columnNames[i] = reverseStr(columnNames[i]);
        }

        boolean tableExist = isTableExists();

        if (!tableExist) {
            String createTableQuery = "CREATE TABLE mtd_table (" + columnNames[0] + " VARCHAR(255) DEFAULT 'Default'";
            // Dynamically generate the CREATE TABLE query
            for (int i = 0; i < columnNames.length; i++) {
                if (i == 0) {
                    existingColumns.add(columnNames[0]);
                    continue;
                }
                String columnName = columnNames[i];
                if (!existingColumns.contains(columnName)) {
                    createTableQuery += ", " + columnName + " VARCHAR(255) DEFAULT 'Default'";
                    existingColumns.add(columnName);
                }
            }
            createTableQuery += ");";
            jdbcTemplate.execute(createTableQuery);
        }
        else {
            for (String columnName : columnNames) {
                if (!existingColumns.contains(columnName)) {
                    //System.out.println(columnName);
                    String updateTableQuery =  "ALTER TABLE mtd_table ADD " + columnName + " VARCHAR(255) DEFAULT 'Default';";
                    existingColumns.add(columnName);
                    jdbcTemplate.execute(updateTableQuery);
                }
            }
        }

        String insertQuery = generateInsertQuery(columnNames);
        System.out.println(insertQuery);
        String newRows[] = new String[rows.length-1];
        System.arraycopy(rows, 1, newRows, 0, newRows.length);

        // Prepare the batch insertion statement
        jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String[] columns = newRows[i].split(",");
                System.out.println(newRows[i]);
                // Set values for each column
                for (int j = 0; j < columnLen; j++) {
                    ps.setString(j + 1, columns[j]);
                    //System.out.println(columns[j]);
                }
            }

            @Override
            public int getBatchSize() {
                return newRows.length;
            }
        });
    }

    private String generateInsertQuery(String[] columnNames) {
        StringBuilder insertQuery = new StringBuilder("INSERT INTO mtd_table (");

        // Append column names
        for (int i = 0; i < columnLen; i++) {
            if (i == 0) {
                insertQuery.append(columnNames[i]);
            } else {
                insertQuery.append(", ").append(columnNames[i]);
            }
        }

        insertQuery.append(") VALUES (");

        // Add placeholders for the columns
        for (int i = 0; i < columnNames.length; i++) {
            if (i == 0) {
                insertQuery.append("?");
            } else {
                insertQuery.append(", ?");
            }
        }

        insertQuery.append(");");

        return insertQuery.toString();
    }


    public boolean isTableExists() {
        String sql = "SELECT EXISTS (SELECT 1 FROM pg_catalog.pg_tables WHERE tablename = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, "mtd_table");
    }

    public String reverseStr(String str) {
        String words[] = str.split(" ");
        StringBuilder reversed = new StringBuilder();

        for (int i = words.length - 1; i >= 0; i--) {
            reversed.append(words[i]);
        }

        return reversed.toString();
    }
}

