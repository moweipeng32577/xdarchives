package com.xdtech.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(scanBasePackages = {"com.xdtech"})
@ServletComponentScan
public class ProjectLotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectLotApplication.class, args);
	}

}

