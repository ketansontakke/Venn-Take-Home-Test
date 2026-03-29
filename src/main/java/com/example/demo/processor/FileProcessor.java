package com.example.demo.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.demo.dto.request.LoadRequest;
import com.example.demo.dto.response.LoadResponse;
import com.example.demo.service.LoadService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FileProcessor {

	private static final Logger log = LoggerFactory.getLogger(FileProcessor.class);

	private final LoadService service;
	private final ObjectMapper mapper = new ObjectMapper();

	public FileProcessor(LoadService service) {
		this.service = service;
	}

	public void processFile(String inputPath, String outputPath) throws Exception {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ClassPathResource(inputPath).getInputStream()));
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

			String line = "";
			int lineNumber = 0;

			try {
				while ((line = reader.readLine()) != null) {
					lineNumber++;

					LoadRequest request = mapper.readValue(line, LoadRequest.class);
					LoadResponse response = service.process(request);

					if (response != null) {
						writer.write(mapper.writeValueAsString(response));
						writer.newLine();
					} else {
						log.warn("Duplicate skipped at line {}", lineNumber);
					}
				}

			} catch (Exception e) {
				log.error("Failed processing line {}: {}", lineNumber, line, e);
			}

		} catch (Exception e) {
			log.error("Failed to process file", e);
			throw new RuntimeException("File processing failed", e);
		}
	}
}
