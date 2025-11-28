package com.example.RDMProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// Al quitar "DataSourceAutoConfiguration" de la exclusión, 
// Spring intentará conectarse a MySQL al arrancar.
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class RdmProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(RdmProjectApplication.class, args);
	}

}