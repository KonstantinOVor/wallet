package com.example.service.wallet.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    private BigDecimal amount;
}
