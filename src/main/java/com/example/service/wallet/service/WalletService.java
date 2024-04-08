package com.example.service.wallet.service;

import com.example.service.wallet.dto.WalletDTO;
import com.example.service.wallet.exception.WalletNotFoundException;
import com.example.service.wallet.model.Wallet;
import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {
    Wallet processWalletOperation(WalletDTO walletDto);

    BigDecimal getWalletBalance(Long walletId) throws WalletNotFoundException;

    void deleteWallet(Long walletId) throws WalletNotFoundException;

    Iterable<Wallet> getWallets();

}
