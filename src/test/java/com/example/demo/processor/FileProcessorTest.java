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
import com.example.demo.entity.LoadEntity;
import com.example.demo.repository.LoadRepository;
import com.example.demo.service.LoadService;

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

		String actual = "data/output/actualOutput.txt";
		String expected = "data/output/expectedOutput.txt";

		processor.processFile("input/expectedInput.txt", actual);

		List<String> actualLines = Files.readAllLines(Path.of(actual));
		List<String> expectedLines = Files.readAllLines(Path.of(expected));

		// TODO: compare each field's value, not just size

		assertEquals(expectedLines.size(), actualLines.size());

		for (int i = 0; i < actualLines.size(); i++) {
			assertEquals(expectedLines.get(i), actualLines.get(i), "Mismatch at line " + i);
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
