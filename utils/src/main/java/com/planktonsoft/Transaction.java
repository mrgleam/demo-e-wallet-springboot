package com.planktonsoft;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    private String transactionId;

    private String sender;

    private String receiver;

    private String purpose;

    private Double amount;
}
