package com.planktonsoft;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PocketService {
    private static final Logger logger = LoggerFactory.getLogger(PocketService.class);

    private final PocketRepository pocketRepository;

    private final TryObjectMapper objectMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = UserConstant.USER_CREATION_TOPIC, groupId = "user-grp")
    public void createWallet(String user) {
        objectMapper.tryReadValue(user, UserMsg.class)
                .map(Pocket::from)
                .map(pocketRepository::save).onFailure(e ->
                    logger.error("Creating wallets: {}", e.getMessage())
                 );
    }

    @KafkaListener(topics = TransactionConstant.TRANSACTION_CREATION_TOPIC, groupId = "transaction-grp")
    @Transactional
    public void updateWalletsForTxn(String msg) throws JsonProcessingException {
        TransactionMsg transaction = objectMapper.readValue(msg, TransactionMsg.class);

        logger.info("Updating wallets: sender - {}, receiver - {}, amount - {}, txnId - {}", transaction.getSender(), transaction.getReceiver(), transaction.getAmount(), transaction.getTransactionId());

        try {
            Pocket senderWallet = pocketRepository.findByPhoneNumber(transaction.getSender());
            Pocket receiverWallet = pocketRepository.findByPhoneNumber(transaction.getReceiver());

            if (senderWallet == null || receiverWallet == null
                    || senderWallet.getBalance() < transaction.getAmount()) {
                handleFailure(transaction);
                return;
            }

            pocketRepository.updatePocket(transaction.getReceiver(), transaction.getAmount()); // +10
            pocketRepository.updatePocket(transaction.getSender(), 0 - transaction.getAmount());  // -10

            transaction.setPocketUpdateStatus(PocketUpdateStatus.SUCCESS);
            kafkaTemplate.send(PocketConstant.POCKET_UPDATED_TOPIC, objectMapper.writeValueAsString(transaction));
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            handleFailure(transaction);
        }
    }

    private void handleFailure(TransactionMsg transaction) throws JsonProcessingException {
        transaction.setPocketUpdateStatus(PocketUpdateStatus.FAILED);
        kafkaTemplate.send(PocketConstant.POCKET_UPDATED_TOPIC, objectMapper.writeValueAsString(transaction));
    }

}
