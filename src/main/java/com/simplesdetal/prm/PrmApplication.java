package com.simplesdetal.prm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class PrmApplication {

	public static void main(String[] args) {

		SpringApplication.run(PrmApplication.class, args);
	}

}
