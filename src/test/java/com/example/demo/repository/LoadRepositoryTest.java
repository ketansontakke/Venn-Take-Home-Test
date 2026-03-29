package com.example.demo.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.demo.entity.LoadEntity;
import com.example.demo.processor.FileProcessor;

@DataJpaTest
public class LoadRepositoryTest {

	@MockitoBean
	private FileProcessor fileProcessor;

	@Autowired
	private LoadRepository repository;

	@Test
	void shouldSaveAndQueryLoads() {
		LoadEntity entity = new LoadEntity();
		entity.setLoadId("1");
		entity.setCustomerId("cust1");
		entity.setAmount(new BigDecimal("100"));
		entity.setTimestamp(Instant.now());
		entity.setAccepted(true);

		repository.save(entity);

		boolean exists = repository.existsByCustomerIdAndLoadId("cust1", "1");
		assertTrue(exists);

		List<LoadEntity> results = repository.findByCustomerIdAndTimestampBetween("cust1",
				Instant.now().minusSeconds(3600), Instant.now().plusSeconds(3600));

		assertFalse(results.isEmpty());
	}
}