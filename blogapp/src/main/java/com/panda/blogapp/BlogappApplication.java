package com.panda.blogapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BlogappApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogappApplication.class, args);
	}

}
