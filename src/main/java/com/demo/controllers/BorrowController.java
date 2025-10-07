package com.demo.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.models.BorrowRecord;

import com.demo.services.BorrowService;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

	@Autowired
	private BorrowService borrowService;
    private static final Logger log = LoggerFactory.getLogger(BorrowController.class);
	
	@GetMapping
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowService.getAllBorrowRecords();
    }

    @GetMapping("/{id}")
    public BorrowRecord getBorrowRecordById(@PathVariable(name="id") String id) {
        return borrowService.getBorrowRecordById(id).orElse(null);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BorrowRecord> createBorrowRecord(@RequestBody BorrowRecord borrowRecord) {
        log.info("POST /api/borrow payload userId={}, bookId={}",
                borrowRecord != null ? borrowRecord.getUserId() : null,
                borrowRecord != null ? borrowRecord.getBookId() : null);
        BorrowRecord created = borrowService.createBorrowRecord(borrowRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public void deleteBorrowRecord(@PathVariable(name="id") String id) {
    	borrowService.deleteBorrowRecord(id);
    }

    @GetMapping("/user/{userId}")
    public List<BorrowRecord> getBorrowRecordsByUserId(@PathVariable(name="userId") String userId) {
        return borrowService.getBorrowRecordsByUserId(userId);
    }


    @GetMapping("/status/{status}")
    public List<BorrowRecord> getBorrowRecordsByStatus(@PathVariable(name="status") String status) {
        return borrowService.getBorrowRecordsByStatus(status);
    }


    @PostMapping("/{id}/return")
    public BorrowRecord returnBook(@PathVariable(name="id") String id) {
        return borrowService.returnBook(id);
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(org.springframework.web.server.ResponseStatusException ex) {
        if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return ex.getReason();
        }
        throw ex;
    }
}

