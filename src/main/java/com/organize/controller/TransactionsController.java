package com.organize.controller;

import com.organize.dto.TransactionDTO;
import com.organize.dto.TransactionResponseDTO;
import com.organize.model.TransactionStatus;
import com.organize.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/admin/transactions")
public class TransactionsController {

    private final TransactionService transactionService;

    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponseDTO> getAllTransactions(
            @RequestHeader("adminId") UUID adminId
    ) {
        return transactionService.getAllEstablishmentTransaction(adminId);
    }

    @GetMapping("/filter")
    public List<TransactionResponseDTO> getTransactionsByPeriod(
            @RequestHeader("adminId") UUID adminId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return transactionService.getEstablishmentTransactionsOnRangeDate(adminId, start, end);
    }

    @PostMapping
    public TransactionResponseDTO createTransaction(
            @RequestHeader("adminId") UUID adminId,
            @RequestBody TransactionDTO transactionDTO
    ) {
        return transactionService.createTransactionForAdmin(adminId, transactionDTO);
    }

    @PatchMapping("/{transactionId}/status")
    public TransactionResponseDTO updateTransactionStatus(
            @RequestHeader("adminId") UUID adminId,
            @PathVariable UUID transactionId,
            @RequestBody Map<String, String> body
    ) {
        String statusStr = body.get("status");
        TransactionStatus status = TransactionStatus.valueOf(statusStr.toUpperCase());
        return transactionService.updateTransactionStatus(adminId, transactionId, status);
    }
}
