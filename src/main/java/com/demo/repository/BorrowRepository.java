package com.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.demo.models.BorrowRecord;



public interface BorrowRepository extends MongoRepository<BorrowRecord, String> {
	List<BorrowRecord> findByUserId(String userId);
	List<BorrowRecord> findByStatus(String status);
}