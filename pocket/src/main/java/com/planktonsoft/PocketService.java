package com.planktonsoft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
@RequiredArgsConstructor
public class PocketService {
    private static final Logger logger = LoggerFactory.getLogger(PocketService.class);

    private final PocketRepository pocketRepository;

    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = TransactionConstant.TRANSACTION_CREATION_TOPIC, groupId = "grp123")
    public void updateWalletsForTxn(String msg) throws ParseException, JsonProcessingException {
        Transaction transaction = objectMapper.readValue(msg, Transaction.class);

        logger.info("Updating wallets: sender - {}, receiver - {}, amount - {}, txnId - {}", transaction.getSender(), transaction.getReceiver(), transaction.getAmount(), transaction.getTransactionId());

        Pocket senderWallet = pocketRepository.findByPhoneNumber(transaction.getSender());
        Pocket receiverWallet = pocketRepository.findByPhoneNumber(transaction.getReceiver());

        if (senderWallet == null || receiverWallet == null
                || senderWallet.getBalance() < transaction.getAmount()) {
//            jsonObject.put("walletUpdateStatus", WalletUpdateStatus.FAILED);
//            kafkaTemplate.send(CommonConstants.WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(jsonObject));
            return;
        }

        pocketRepository.updatePocket(transaction.getReceiver(), transaction.getAmount()); // +10
        pocketRepository.updatePocket(transaction.getSender(), 0 - transaction.getAmount());  // -10

//        jsonObject.put("walletUpdateStatus", WalletUpdateStatus.SUCCESS);
//        kafkaTemplate.send(CommonConstants.WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(jsonObject));
    }
}
