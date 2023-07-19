package com.gunam.app.Controller;

import com.gunam.app.Repository.DataInsertion;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MTDFileController {
    private DataInsertion dataInsertion;

    public MTDFileController() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/gunam");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");

        dataInsertion = new DataInsertion(dataSource);
    }
    public void createAndInsertData(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String dataString = "";
            while ((line = reader.readLine()) != null) {
                dataString += line + "\n";
            }
            dataInsertion.createAndInsertData(dataString);
        } catch (IOException e) {
            throw new IOException("IO Exception");
        }
    }

}
