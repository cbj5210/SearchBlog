package com.search.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SearchBlogApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchBlogApiApplication.class, args);
	}
}
