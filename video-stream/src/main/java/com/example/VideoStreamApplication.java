package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.content.fs.config.EnableFilesystemStores;

@SpringBootApplication
@EnableFilesystemStores
public class VideoStreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoStreamApplication.class, args);
	}

}
