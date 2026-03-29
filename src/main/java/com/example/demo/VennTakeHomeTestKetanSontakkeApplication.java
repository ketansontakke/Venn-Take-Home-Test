package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.processor.FileProcessor;

@SpringBootApplication
public class VennTakeHomeTestKetanSontakkeApplication {

	@Value("${demo.output.path}")
	private static final String OUTPUT_PATH = "data/output/actualOutput.txt";
	private static final String INPUT_PATH = "input/expectedInput.txt";

	public static void main(String[] args) {
		SpringApplication.run(VennTakeHomeTestKetanSontakkeApplication.class, args);
	}

	@Bean
	CommandLineRunner run(FileProcessor processor) {
		return args -> {
			processor.processFile(INPUT_PATH, OUTPUT_PATH);
		};
	}

}
