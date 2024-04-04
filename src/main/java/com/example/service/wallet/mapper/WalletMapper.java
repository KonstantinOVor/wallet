package com.example.service.wallet.mapper;

import com.example.service.wallet.dto.WalletDTO;
import com.example.service.wallet.model.Wallet;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import java.util.UUID;

@DecoratedWith(WalletMapperDelegate.class)
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface WalletMapper {

    Wallet mapWalletDTOToWallet(WalletDTO walletDTO);

    UUID map(Long longWalletId);
}
