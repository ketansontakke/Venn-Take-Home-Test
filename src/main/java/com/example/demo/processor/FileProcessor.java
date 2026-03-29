package com.example.demo.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.demo.dto.request.LoadRequest;
import com.example.demo.dto.response.LoadResponse;
import com.example.demo.exception.LoadRejectedException;
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

	public void processFile(String inputPath, String outputPath) {

		log.info("Starting file processing. Input={}, Output={}", inputPath, outputPath);

		// Ensure output directory exists
		File outputFile = new File(outputPath);
		outputFile.getParentFile().mkdirs();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ClassPathResource(inputPath).getInputStream()));
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

			String line;
			int lineNumber = 0;

			while ((line = reader.readLine()) != null) {
				lineNumber++;

				try {
					LoadRequest request = mapper.readValue(line, LoadRequest.class);

					LoadResponse response = service.process(request);

					// Accepted case
					writer.write(mapper.writeValueAsString(response));
					writer.newLine();

				} catch (LoadRejectedException ex) {
					// Rejected case: still write output (accepted=false)
					log.warn("Rejected load at line {}: {} - {}", lineNumber, ex.getCode(), ex.getMessage());

					LoadRequest request = mapper.readValue(line, LoadRequest.class);

					LoadResponse rejectedResponse = new LoadResponse(request.getId(), request.getCustomer_id(), false);

					writer.write(mapper.writeValueAsString(rejectedResponse));
					writer.newLine();

				} catch (Exception ex) {
					// Bad input or unexpected error
					log.error("Failed processing line {}: {}", lineNumber, line, ex);
				}
			}

			log.info("File processing completed successfully");

		} catch (Exception e) {
			log.error("Failed to process file", e);
			throw new RuntimeException("File processing failed", e);
		}
	}
}