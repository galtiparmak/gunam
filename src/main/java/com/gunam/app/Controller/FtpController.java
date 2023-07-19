package com.gunam.app.Controller;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FtpController {

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
                    mtdFile.createAndInsertData("ftp-downloads/" + remoteFileName);
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
        String localFilePath = "ftp-downloads/" + remoteFileName;
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
}
