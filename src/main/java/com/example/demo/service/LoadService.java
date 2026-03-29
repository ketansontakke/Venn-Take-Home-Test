package com.example.demo.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.LoadRequest;
import com.example.demo.dto.response.LoadResponse;
import com.example.demo.entity.LoadEntity;
import com.example.demo.repository.LoadRepository;
import com.example.demo.util.LoadUtils;

@Service
public class LoadService {

	private static final Logger log = LoggerFactory.getLogger(LoadService.class);

	private static final BigDecimal DAILY_LIMIT = new BigDecimal("5000");
	private static final BigDecimal WEEKLY_LIMIT = new BigDecimal("20000");
	private static final int DAILY_COUNT_LIMIT = 3;

	private final LoadRepository repository;

	public LoadService(LoadRepository repository) {
		this.repository = repository;
	}

	public LoadResponse process(LoadRequest request) {

		log.info("Processing load request: id={}, customer={}", request.getId(), request.getCustomer_id());

		try {
			// Idempotency check
			if (repository.existsByCustomerIdAndLoadId(request.getCustomer_id(), request.getId())) {

				log.warn("Duplicate load detected: id={}, customer={}", request.getId(), request.getCustomer_id());
				return null; // ignore duplicate
			}

			BigDecimal amount = LoadUtils.parseAmount(request.getLoad_amount());
			Instant timestamp = Instant.parse(request.getTime());

			Instant dayStart = LoadUtils.startOfDay(timestamp);
			Instant dayEnd = LoadUtils.endOfDay(timestamp);

			Instant weekStart = LoadUtils.startOfWeek(timestamp);
			Instant weekEnd = LoadUtils.endOfWeek(timestamp);

			List<LoadEntity> dailyLoads = repository.findByCustomerIdAndTimestampBetween(request.getCustomer_id(),
					dayStart, dayEnd);

			List<LoadEntity> weeklyLoads = repository.findByCustomerIdAndTimestampBetween(request.getCustomer_id(),
					weekStart, weekEnd);

			BigDecimal dailySum = dailyLoads.stream().filter(LoadEntity::isAccepted).map(LoadEntity::getAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal weeklySum = weeklyLoads.stream().filter(LoadEntity::isAccepted).map(LoadEntity::getAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			long dailyCount = dailyLoads.stream().filter(LoadEntity::isAccepted).count();

			boolean accepted = dailySum.add(amount).compareTo(DAILY_LIMIT) <= 0
					&& weeklySum.add(amount).compareTo(WEEKLY_LIMIT) <= 0 && dailyCount < DAILY_COUNT_LIMIT;

			log.info("Decision for id={}: accepted={}", request.getId(), accepted);

			// Persist result
			LoadEntity entity = new LoadEntity();
			entity.setLoadId(request.getId());
			entity.setCustomerId(request.getCustomer_id());
			entity.setAmount(amount);
			entity.setTimestamp(timestamp);
			entity.setAccepted(accepted);

			repository.save(entity);

			return new LoadResponse(request.getId(), request.getCustomer_id(), accepted);

		} catch (Exception e) {
			log.error("Error processing load request id={}", request.getId(), e);
			throw e;
		}
	}
}