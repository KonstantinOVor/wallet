package com.example.service.wallet.mapper;

import com.example.service.wallet.dto.WalletDTO;
import com.example.service.wallet.model.OperationType;
import com.example.service.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class WalletMapperDelegate implements WalletMapper {

    @Override
    public Wallet mapWalletDTOToWallet(WalletDTO walletDTO) {
        return Wallet.builder()
                .id(map(walletDTO.getWalletId()))
                .type(walletDTO.getType().equalsIgnoreCase("DEPOSIT") ? OperationType.DEPOSIT
                        : OperationType.WITHDRAW)
                .amount(BigDecimal.valueOf(walletDTO.getAmount()))
                .build();
    }

    @Override
    public UUID map(Long longWalletId) {
        if (longWalletId == null) {
            return null;
        }
        byte[] bytes = longWalletId.toString().getBytes();
        return UUID.nameUUIDFromBytes(bytes);
    }
}

