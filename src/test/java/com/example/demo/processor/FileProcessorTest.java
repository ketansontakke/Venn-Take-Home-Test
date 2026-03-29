package com.example.demo.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.dto.request.LoadRequest;
import com.example.demo.dto.response.LoadResponse;
import com.example.demo.entity.LoadEntity;
import com.example.demo.repository.LoadRepository;
import com.example.demo.service.LoadService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class FileProcessorTest {

	private LoadRequest loadRequest;

	@Autowired
	private LoadService loadService;

	@Autowired
	private LoadRepository repository;

	@Autowired
	private FileProcessor processor;

	@BeforeEach
	void cleanUpRepo() {
		repository.deleteAll(); // ensure no pre-existing records before each test
	}

	@Test
	void shouldProcessFileAndGenerateOutput() throws Exception {

		String outputPath = "data/test-output/test-output.txt";
		new File(outputPath).getParentFile().mkdirs();

		processor.processFile("input/expectedInput.txt", outputPath);

		File file = new File(outputPath);
		assertTrue(file.exists());

		String content = Files.readString(file.toPath());
		assertFalse(content.isEmpty());
	}

	@Test
	void shouldMatchExpectedOutput() throws Exception {

		String actualOutput = "data/output/actualOutput.txt";
		String expectedOutput = "data/output/expectedOutput.txt";

		processor.processFile("input/expectedInput.txt", actualOutput);

		ObjectMapper mapper = new ObjectMapper();

		List<LoadResponse> actualList = Files.readAllLines(Path.of(actualOutput)).stream().map(line -> {
			try {
				return mapper.readValue(line, LoadResponse.class);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).toList();

		List<LoadResponse> expectedList = Files.readAllLines(Path.of(expectedOutput)).stream().map(line -> {
			try {
				return mapper.readValue(line, LoadResponse.class);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).toList();

		// First check size
		assertEquals(expectedList.size(), actualList.size(), "List sizes differ");

		// Compare each field
		for (int i = 0; i < actualList.size(); i++) {

			LoadResponse actual = actualList.get(i);
			LoadResponse expected = expectedList.get(i);

			assertEquals(expected.getId(), actual.getId(), "Mismatch at index " + i + " for id");
			assertEquals(expected.getCustomer_id(), actual.getCustomer_id(),
					"Mismatch at index " + i + " for customer_id");
			assertEquals(expected.isAccepted(), actual.isAccepted(), "Mismatch at index " + i + " for accepted");
		}
	}

	@Test
	void shouldProcessLargeFileQuickly() throws Exception {

		long start = System.currentTimeMillis();

		processor.processFile("input/expectedInput.txt", "data/output/performance.txt");

		long duration = System.currentTimeMillis() - start;

		System.out.println("Execution time: " + duration + "ms");

		assertTrue(duration < 5000); // under 5 seconds
	}

	@Test
	void shouldHandleConcurrentRequests() throws Exception {

		loadRequest = new LoadRequest();
		loadRequest.setCustomer_id("cust1");
		loadRequest.setLoad_amount("$100");
		loadRequest.setTime("2020-01-01T00:00:00Z");

		int threads = 10;
		ExecutorService executor = Executors.newFixedThreadPool(threads);

		List<Callable<Void>> tasks = new ArrayList<>();

		for (int i = 0; i < 50; i++) {
			int id = i;

			loadRequest.setId(String.valueOf(id));

			tasks.add(() -> {
				loadService.process(loadRequest);
				return null;
			});
		}

		executor.invokeAll(tasks);
		executor.shutdown();

		// Validate no crashes and reasonable DB state
		List<LoadEntity> all = repository.findAll();
		assertTrue(all.size() <= 1000); // 1000 is the input size, can dynamically set this based on input size after.
	}

}
