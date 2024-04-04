package com.example.service.wallet.model;

public enum OperationType {
    DEPOSIT("deposit"),
    WITHDRAW("withdraw");

    private final String str;

    OperationType(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}
