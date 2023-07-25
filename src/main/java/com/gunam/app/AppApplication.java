package com.gunam.app;

import com.gunam.app.Configuration.CorsConfig;
import com.gunam.app.Controller.FtpController;
import com.gunam.app.Controller.MTDFileController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@ComponentScan("com.gunam.app.Controller")
public class AppApplication {

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext context = SpringApplication.run(AppApplication.class, args);
		FtpController ftpController = context.getBean(FtpController.class);
		//ftpController.downloadFilesInDirectory();
		//ftpController.downloadFileAtMidnight();

		//context.close();
	}
}


