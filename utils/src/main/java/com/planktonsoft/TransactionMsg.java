package com.planktonsoft;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionMsg {
    private String transactionId;

    private String sender;

    private String receiver;

    private String purpose;

    private Double amount;

    @Enumerated(value = EnumType.STRING)
    private PocketUpdateStatus pocketUpdateStatus;
}
