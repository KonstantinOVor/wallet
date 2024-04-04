package com.example.service.wallet.dto;

import com.example.service.wallet.validation.EnumNamePattern;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {
    private Long walletId;
    @EnumNamePattern
    private String type;
    @Min(value = 0, message = "The amount must be greater than or equal to zero")
    private Long amount;
}