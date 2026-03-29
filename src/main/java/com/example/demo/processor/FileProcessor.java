package com.example.demo.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.demo.dto.request.LoadRequest;
import com.example.demo.dto.response.LoadResponse;
import com.example.demo.service.LoadService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FileProcessor {

	private final LoadService service;
	private final ObjectMapper mapper = new ObjectMapper();

	public FileProcessor(LoadService service) {
		this.service = service;
	}

	public void processFile(String inputPath, String outputPath) throws Exception {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ClassPathResource(inputPath).getInputStream()));
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

			String line;

			while ((line = reader.readLine()) != null) {

				LoadRequest request = mapper.readValue(line, LoadRequest.class);
				LoadResponse response = service.process(request);

				if (response != null) {
					writer.write(mapper.writeValueAsString(response));
					writer.newLine();
				}
			}
		}
	}
}
