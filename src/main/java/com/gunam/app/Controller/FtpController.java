package com.gunam.app.Controller;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@EnableScheduling
public class FtpController {
    private final String DOWNLOAD_FOLDER = "ftp-downloads/";
    private String remoteFileName;
    private String remoteDirectory;
    MTDFileController mtdFile;
    FTPClient ftpClient;

    public FtpController() {
        mtdFile = new MTDFileController();
        this.remoteDirectory = "mtd";
        ftpClient = new FTPClient();
    }

    public void setRemoteFileNames(String remoteFileName) {
        this.remoteFileName = remoteFileName;
    }

    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    @PostConstruct
    public void downloadFilesInDirectory() {
        String server = "144.122.31.54";
        int port = 21;
        String username = "data_admin";
        String password = "da_xrd3200";

        try {
            // Connect to the FTP server and login
            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Set the remote directory
            ftpClient.changeWorkingDirectory(remoteDirectory);

            // List the files in the directory
            FTPFile[] files = ftpClient.listFiles();

            // Download each file in the directory
            for (FTPFile file : files) {
                if (file.isFile()) {
                    this.remoteFileName = file.getName();
                    System.out.println(remoteFileName);
                    downloadFile();
                    mtdFile.createAndInsertData(DOWNLOAD_FOLDER + remoteFileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Disconnect from the FTP server
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void downloadFile() {

        String remoteFilePath = "/" + remoteDirectory + "/" + remoteFileName;
        System.out.println(remoteFilePath);
        String localFilePath = DOWNLOAD_FOLDER + remoteFileName;
        try {

            Path localPath = Paths.get(localFilePath);
            Files.createDirectories(localPath.getParent());

            FileOutputStream outputStream = new FileOutputStream(localFilePath);
            boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
            outputStream.close();

            if (success) {
                System.out.println("File downloaded successfully.");
            } else {
                System.out.println("File download failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Scheduled(cron = "0 0 00 * * *") // Runs at midnight (00:00) every day
    public void downloadFileAtMidnight() {

        String server = "144.122.31.54";
        int port = 21;
        String username = "data_admin";
        String password = "da_xrd3200";

        //this.remoteFileName = getYesterdaysDate();
        this.remoteFileName = "mt01012016.mtd";

        String remoteFilePath = "/" + remoteDirectory + "/" + remoteFileName;
        System.out.println("File path yesterday: " + remoteFilePath);
        String localFilePath = DOWNLOAD_FOLDER + remoteFileName;

        try {
            // Connect to the FTP server and login
            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            Path localPath = Paths.get(localFilePath);
            Files.createDirectories(localPath.getParent());

            FileOutputStream outputStream = new FileOutputStream(localFilePath);
            boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
            outputStream.close();

            if (success) {
                System.out.println("File downloaded successfully.");
                mtdFile.createAndInsertData(DOWNLOAD_FOLDER + remoteFileName);
            } else {
                System.out.println("File download failed.");
            }

            this.remoteFileName = getOneMonthBefore();
            remoteFilePath = "/" + remoteDirectory + "/" + remoteFileName;
            System.out.println("File path one month before: " + remoteFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Disconnect from the FTP server
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Scheduled(cron = "0 1 00 * * *") // Runs at one minute after midnight (00:01) every day
    public void deleteFileAtMidnight() {

        String server = "144.122.31.54";
        int port = 21;
        String username = "data_admin";
        String password = "da_xrd3200";

        //this.remoteFileName = getOneMonthBefore();
        this.remoteFileName = "mt01012016.mtd";

        String remoteFilePath = "/" + remoteDirectory + "/" + remoteFileName;
        System.out.println("File path one month before: " + remoteFilePath);
        String localFilePath = DOWNLOAD_FOLDER + remoteFileName;

        try {
            // Connect to the FTP server and login
            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();

            boolean success = ftpClient.deleteFile(remoteFilePath);;

            if (success) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("File deletion failed: File does not exist.");
            }

        } catch (IOException e) {
            System.out.println("File deletion failed: " + e.getMessage());
        } finally {
            // Disconnect from the FTP server
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String getYesterdaysDate() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy");
        String formattedYesterday = "mt" + yesterday.format(formatter) + ".mtd";

        return formattedYesterday;
    }

    public String getOneMonthBefore() {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthBefore = today.minusMonths(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy");
        String formattedOneMonthBefore = "mt" + oneMonthBefore.format(formatter) + ".mtd";

        return formattedOneMonthBefore;
    }

}
