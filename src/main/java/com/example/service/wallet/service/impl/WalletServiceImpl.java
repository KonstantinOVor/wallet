package com.example.service.wallet.service.impl;

import com.example.service.wallet.dto.WalletDTO;
import com.example.service.wallet.exception.InsufficientBalanceException;
import com.example.service.wallet.exception.TransactionException;
import com.example.service.wallet.exception.WalletNotFoundException;
import com.example.service.wallet.mapper.WalletMapper;
import com.example.service.wallet.model.OperationType;
import com.example.service.wallet.model.Wallet;
import com.example.service.wallet.repository.WalletRepository;
import com.example.service.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public Wallet processWalletOperation(WalletDTO walletDto) {
        log.info("Processing wallet operation: {}", walletDto.getWalletId());

        Wallet wallet = createWallet(walletDto);

        Wallet existingWallet = walletRepository.findById(wallet.getId())
                .map(w -> {
                    try {
                        Optional<BigDecimal> newAmount = calculateNewAmount(w, wallet);
                        newAmount.ifPresent(w::setAmount);
                    } catch (InsufficientBalanceException | TransactionException e) {
                        log.error("Failed to process wallet operation", e);
                        e.printStackTrace();
                    }
                    return w;
                })
                .orElseGet(() -> wallet);

        return walletRepository.save(existingWallet);
    }

    private Optional<BigDecimal> calculateNewAmount(Wallet existingWallet, Wallet wallet) throws InsufficientBalanceException, TransactionException {
        log.info("Calculating new amount for wallet with ID: {}", wallet.getId());

        BigDecimal amount;
        if (wallet.getType() == OperationType.DEPOSIT) {
            amount = existingWallet.getAmount().add(wallet.getAmount());
        } else if (wallet.getType() == OperationType.WITHDRAW) {
            amount = existingWallet.getAmount().subtract(wallet.getAmount());

            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                log.error("Insufficient funds to change the balance sheet");
                throw new InsufficientBalanceException("Insufficient funds to change the balance sheet");
            }
        } else {
            log.error("Unsupported operation type");
            throw new TransactionException("Unsupported operation type");
        }
        return Optional.of(amount);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BigDecimal getWalletBalance(Long walletId) throws WalletNotFoundException {
        log.info("Getting balance for wallet with ID: {}", walletId);

        UUID mappedId = map(walletId);
        Wallet existingWallet = walletRepository.findById(Objects.requireNonNull(mappedId))
                .orElseThrow(() -> new WalletNotFoundException("Wallet with ID " + walletId + " not found"));

        return existingWallet.getAmount();
    }

    @Override
    public void deleteWallet(Long walletId) throws WalletNotFoundException {
        log.info("Deleting wallet with ID: {}", walletId);

        UUID mappedId = map(walletId);
        Wallet wallet = walletRepository.findById(Objects.requireNonNull(map(walletId)))
                .orElseThrow(() -> new WalletNotFoundException("Wallet with ID " + walletId + " not found"));
        walletRepository.delete(wallet);
    }

    @Override
    public Object getWallets() {
        log.info("Getting all wallets");

        return walletRepository.findAll();
    }


    private Wallet createWallet(WalletDTO walletDto) {
        log.info("Creating wallet: {}", walletDto.getWalletId());

        return walletMapper.mapWalletDTOToWallet(walletDto);
    }

    private UUID map(Long longWalletId) {
        log.info("Mapping wallet ID: {}", longWalletId);

        if (longWalletId == null) {
            return null;
        }
        return walletMapper.map(longWalletId);
    }
}
