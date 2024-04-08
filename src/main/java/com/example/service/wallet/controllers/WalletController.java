package com.example.service.wallet.controllers;

import com.example.service.wallet.dto.WalletDTO;
import com.example.service.wallet.exception.WalletNotFoundException;
import com.example.service.wallet.model.Wallet;
import com.example.service.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
@Slf4j
@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Object> createWallet(@RequestBody @Valid WalletDTO walletDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.processWalletOperation(walletDto));
    }

    @GetMapping
    public ResponseEntity<Iterable<Wallet>> getWallets() {

        return ResponseEntity.status(HttpStatus.OK).body(walletService.getWallets());
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Object> getBalanceOfWallet(@PathVariable Long walletId) {

        try {
            BigDecimal balance = walletService.getWalletBalance(walletId);
            return ResponseEntity.ok(balance);
        } catch (WalletNotFoundException e) {
            log.error("Wallet not found for ID: {}", walletId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/{walletId}")
    public ResponseEntity<Object> deleteWallet(@PathVariable Long walletId) {

        try {
            walletService.deleteWallet(walletId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (WalletNotFoundException e) {
            log.error("Wallet not found for ID: {}", walletId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
