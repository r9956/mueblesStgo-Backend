package com.example.mueblesStgoBackend;

import com.example.mueblesStgoBackend.services.ClockDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class MueblesStgoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MueblesStgoBackendApplication.class, args);
	}
}
