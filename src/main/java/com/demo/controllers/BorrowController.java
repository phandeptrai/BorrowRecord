package com.demo.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.models.BorrowRecord;

import com.demo.services.BorrowService;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

	@Autowired
	private BorrowService borrowService;
	
	@GetMapping
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowService.getAllBorrowRecords();
    }

    @GetMapping("/{id}")
    public BorrowRecord getBorrowRecordById(@PathVariable(name="id") String id) {
        return borrowService.getBorrowRecordById(id).orElse(null);
    }

    @PostMapping
    public BorrowRecord createBorrowRecord(@RequestBody BorrowRecord borrowRecord) {
        return borrowService.createBorrowRecord(borrowRecord);
    }

    @DeleteMapping("/{id}")
    public void deleteBorrowRecord(@PathVariable(name="id") String id) {
    	borrowService.deleteBorrowRecord(id);
    }

    @GetMapping("/user/{userId}")
    public List<BorrowRecord> getBorrowRecordsByUserId(@PathVariable String userId) {
        return borrowService.getBorrowRecordsByUserId(userId);
    }


    @GetMapping("/status/{status}")
    public List<BorrowRecord> getBorrowRecordsByStatus(@PathVariable String status) {
        return borrowService.getBorrowRecordsByStatus(status);
    }


    @PostMapping("/{id}/return")
    public BorrowRecord returnBook(@PathVariable String id) {
        return borrowService.returnBook(id);
    }
}

