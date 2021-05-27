package com.ptit.etax.repository;

import com.ptit.etax.model.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PayRepository extends JpaRepository<Pay, String> {
	List<Pay> findAllByTaxId(String taxId);

	boolean existsByTaxIdAndCalculateDateBetween(String taxId, Instant firstDayOfMonth, Instant endDayOfMonth);
}
