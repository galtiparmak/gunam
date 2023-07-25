package com.gunam.app.Configuration;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.io.IOException;

//@Service
//public class FileDownloadService {
//
//    public void downloadFileFromFTP(String server, int port, String username, String password, String remoteFilePath, String localFilePath) throws IOException {
//        FTPClient ftpClient = new FTPClient();
//        try {
//            ftpClient.connect(server, port);
//            ftpClient.login(username, password);
//            ftpClient.enterLocalPassiveMode();
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//
//            FileOutputStream outputStream = new FileOutputStream(localFilePath);
//            boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
//            outputStream.close();
//
//            if (success) {
//                System.out.println("File downloaded successfully.");
//            } else {
//                System.out.println("File download failed.");
//            }
//        } finally {
//            ftpClient.disconnect();
//        }
//    }
//}
