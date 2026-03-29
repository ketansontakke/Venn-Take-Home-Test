package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.dto.request.LoadRequest;
import com.example.demo.dto.response.LoadResponse;
import com.example.demo.entity.LoadEntity;
import com.example.demo.repository.LoadRepository;

public class LoadServiceTest {

	private LoadRepository repository;
	private LoadService service;

	@BeforeEach
	void setup() {
		repository = mock(LoadRepository.class);
		service = new LoadService(repository);
	}

	private LoadRequest request(String id, String customer, String amount, String time) {
		LoadRequest r = new LoadRequest();
		r.setId(id);
		r.setCustomer_id(customer);
		r.setLoad_amount(amount);
		r.setTime(time);
		return r;
	}

	@Test
	void shouldAcceptValidLoad() {
		when(repository.existsByCustomerIdAndLoadId(any(), any())).thenReturn(false);
		when(repository.findByCustomerIdAndTimestampBetween(any(), any(), any())).thenReturn(List.of());

		LoadResponse response = service.process(request("1", "cust1", "$1000.00", "2020-01-01T00:00:00Z"));

		assertNotNull(response);
		assertTrue(response.isAccepted());
	}

	@Test
	void shouldRejectIfDailyAmountExceeded() {
		when(repository.existsByCustomerIdAndLoadId(any(), any())).thenReturn(false);

		LoadEntity existing = new LoadEntity();
		existing.setAmount(new BigDecimal("4500"));
		existing.setAccepted(true);

		when(repository.findByCustomerIdAndTimestampBetween(any(), any(), any())).thenReturn(List.of(existing));

		LoadResponse response = service.process(request("2", "cust1", "$600.00", "2020-01-01T01:00:00Z"));

		assertFalse(response.isAccepted());
	}

	@Test
	void shouldRejectIfDailyCountExceeded() {
		when(repository.existsByCustomerIdAndLoadId(any(), any())).thenReturn(false);

		LoadEntity l = new LoadEntity();
		l.setAccepted(true);
		l.setAmount(new BigDecimal("100"));

		when(repository.findByCustomerIdAndTimestampBetween(any(), any(), any())).thenReturn(List.of(l, l, l));

		LoadResponse response = service.process(request("3", "cust1", "$100.00", "2020-01-01T02:00:00Z"));

		assertFalse(response.isAccepted());
	}

	@Test
	void shouldIgnoreDuplicateLoadId() {
		when(repository.existsByCustomerIdAndLoadId(any(), any())).thenReturn(true);

		LoadResponse response = service.process(request("1", "cust1", "$100", "2020-01-01T00:00:00Z"));

		assertNull(response);
	}

	@Test
	void shouldRejectIfWeeklyLimitExceeded() {
		when(repository.existsByCustomerIdAndLoadId(any(), any())).thenReturn(false);

		LoadEntity existing = new LoadEntity();
		existing.setAmount(new BigDecimal("19500"));
		existing.setAccepted(true);

		when(repository.findByCustomerIdAndTimestampBetween(any(), any(), any())).thenReturn(List.of(existing));

		LoadResponse response = service.process(request("10", "cust1", "$1000", "2020-01-02T00:00:00Z"));

		assertFalse(response.isAccepted());
	}
}
