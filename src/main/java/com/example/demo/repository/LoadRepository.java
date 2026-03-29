package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.LoadEntity;

import java.time.Instant;
import java.util.List;

public interface LoadRepository extends JpaRepository<LoadEntity, Long> {

    boolean existsByCustomerIdAndLoadId(String customerId, String loadId);

    List<LoadEntity> findByCustomerIdAndTimestampBetween(
            String customerId,
            Instant start,
            Instant end
    );
}
