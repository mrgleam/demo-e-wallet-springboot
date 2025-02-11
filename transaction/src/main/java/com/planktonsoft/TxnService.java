package com.planktonsoft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TxnService {

    private final TransactionRepository transactionRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(TxnService.class);

    public String initiateTxn(String sender, String receiver, String purpose, Double amount) throws JsonProcessingException {
        logger.info("Inside initiateTxn method with sender - {}, receiver - {}, purpose - {}", sender, receiver, purpose);

        Transaction transaction = Transaction.builder()
                .sender(sender)
                .receiver(receiver)
                .purpose(purpose)
                .transactionId(UUID.randomUUID().toString())
                .transactionStatus(TransactionStatus.PENDING)
                .amount(amount)
                .build();

        transactionRepository.save(transaction);

        kafkaTemplate.send(TransactionConstant.TRANSACTION_CREATION_TOPIC, objectMapper.writeValueAsString(transaction));

        return transaction.getTransactionId();
    }

    @KafkaListener(topics = PocketConstant.POCKET_UPDATED_TOPIC, groupId = "pocket-grp")
    public void updateTxn(String msg) throws ParseException, JsonProcessingException {
        TransactionMsg transactionMsg = objectMapper.readValue(msg, TransactionMsg.class);
        String txnId = transactionMsg.getTransactionId();
        logger.info("Updating txn: sender - {}, receiver - {}, amount - {}, txnId - {}", transactionMsg.getSender(), transactionMsg.getReceiver(), transactionMsg.getAmount(), transactionMsg.getTransactionId());

        PocketUpdateStatus pocketUpdateStatus = transactionMsg.getPocketUpdateStatus();

        if (pocketUpdateStatus == PocketUpdateStatus.SUCCESS) {
            transactionRepository.updateTxn(txnId, TransactionStatus.SUCCESSFUL);
        } else {
            transactionRepository.updateTxn(txnId, TransactionStatus.FAILED);
        }
    }
}
