
package com.cloudops.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class CloudopsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				CloudopsBackendApplication.class,
				args
		);
	}
}
