package com.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.models.BorrowRecord;
import com.demo.repository.BorrowRepository;


@Service
public class BorrowService {
	 @Autowired
	    private BorrowRepository borrowRepository;

	 @Autowired
	 private ValidationService validationService;

		public List<BorrowRecord> getAllBorrowRecords() {
			return borrowRepository.findAll();
		}

		public Optional<BorrowRecord> getBorrowRecordById(String id) {
			return borrowRepository.findById(id);
		}

		public BorrowRecord createBorrowRecord(BorrowRecord borrowRecord) {
			if (borrowRecord == null || borrowRecord.getUserId() == null || borrowRecord.getBookId() == null) {
				throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "userId and bookId are required");
			}
			validationService.assertUserAndBookExist(borrowRecord.getUserId(), borrowRecord.getBookId());
			return borrowRepository.save(borrowRecord);
		}

		public void deleteBorrowRecord(String id) {
			borrowRepository.deleteById(id);
		}

		// Tìm kiếm bản ghi mượn theo userId
		public List<BorrowRecord> getBorrowRecordsByUserId(String userId) {
			return borrowRepository.findByUserId(userId);
		}

		// Lấy danh sách bản ghi mượn theo trạng thái
		public List<BorrowRecord> getBorrowRecordsByStatus(String status) {
			return borrowRepository.findByStatus(status);
		}

		// Cập nhật trạng thái trả sách
		public BorrowRecord returnBook(String id) {
			Optional<BorrowRecord> recordOpt = borrowRepository.findById(id);
			if (recordOpt.isPresent()) {
				BorrowRecord record = recordOpt.get();
				record.setStatus(com.demo.models.BorrowStatus.RETURNED);
				record.setReturnDate(java.time.LocalDate.now());
				return borrowRepository.save(record);
			}
			return null;
		}
}
