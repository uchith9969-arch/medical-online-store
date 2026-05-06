package com.example.medical_online_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class
})
public class MedicalOnlineStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicalOnlineStoreApplication.class, args);
	}

}
