package com.planktonsoft;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.concurrent.atomic.AtomicReference;

import static io.vavr.API.*;

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
                    logger.error("Creating pockets: {}", e.getMessage())
                 );
    }

    @KafkaListener(topics = TransactionConstant.TRANSACTION_CREATION_TOPIC, groupId = "transaction-grp")
    @Transactional
    public void processTransfer(String msg) {
        // Use an AtomicReference to store the TransactionMsg for use in the error branch.
        AtomicReference<TransferRequest> transferHolder = new AtomicReference<>();

        Either<BusinessError, Boolean> result = parseTransferRequest(msg)
                // Log the transfer details and store them.
                .peek(transferDetails -> {
                    logTransfer(transferDetails);
                    transferHolder.set(transferDetails);
                })
                // Validate sender and receiver existence and check sender balance.
                .flatMap(this::validateTransferParticipants)
                // Update the balances.
                .flatMap(this::updateBalances)
                // Send the Kafka success notification.
                .flatMap(this::notifySuccess);

        result.fold(
                error -> {
                    logger.error("Unexpected error occurred: {}", error.getMessage());
                    // Mark the transaction for rollback.
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    // If available, call the failure handler using the original transaction.
                    TransferRequest tx = transferHolder.get();
                    if (tx != null) {
                        handleFailure(tx);
                    }
                    return null;
                },
                success -> {
                    logger.info("Pocket update and Kafka notification succeeded");
                    return success;
                }
        );

    }

    // Convert JSON string into a TransferRequest.
    private Either<BusinessError, TransferRequest> parseTransferRequest(String msg) {
        return Try.of(() -> objectMapper.readValue(msg, TransferRequest.class))
                .toEither()
                .mapLeft(throwable -> new BusinessError("Error parsing message: " + throwable.getMessage()));
    }

    // Log the transaction details.
    private void logTransfer(TransferRequest transaction) {
        logger.info("Updating wallets: sender - {}, receiver - {}, amount - {}, txnId - {}",
                transaction.getSender(),
                transaction.getReceiver(),
                transaction.getAmount(),
                transaction.getTransactionId());
    }

    // Find the sender's pocket.
    private Either<BusinessError, Pocket> findSenderAccount(String senderPhoneNumber) {
        return pocketRepository.findByPhoneNumber(senderPhoneNumber)
                .toEither(new BusinessError("Sender not found"));
    }

    // Find the receiver's pocket.
    private Either<BusinessError, Pocket> findReceiverAccount(String receiverPhoneNumber) {
        return pocketRepository.findByPhoneNumber(receiverPhoneNumber)
                .toEither(new BusinessError("Receiver not found"));
    }

    private Either<BusinessError, TransferRequest> validateTransferParticipants(TransferRequest transferDetails) {
        return For(
                        findSenderAccount(transferDetails.getSender()),
                        findReceiverAccount(transferDetails.getReceiver())
                ).yield((sender, _receiver) -> new SenderAccount(sender))
                .toEither(new BusinessError("Receiver not found"))
                .flatMap(senderAccount -> senderAccount.hasSufficientFunds(transferDetails.getAmount()))
                .map(validSender -> transferDetails);
    }

    // Update pockets: subtract from sender and add to receiver.
    // Returns the original TransferRequest on success.
    public Either<BusinessError, TransferRequest> updateBalances(TransferRequest transaction) {
        return Try.of(() -> {
                    pocketRepository.updatePocket(transaction.getReceiver(), transaction.getAmount());   // +amount to receiver
                    pocketRepository.updatePocket(transaction.getSender(), -transaction.getAmount());    // -amount from sender
                    return transaction;
                }).toEither()
                .mapLeft(e -> new BusinessError("Error updating pockets: " + e.getMessage()));
    }

    // Send a Kafka message to notify success.
    // Returns a Boolean indicator wrapped in an Either.
    private Either<BusinessError, Boolean> notifySuccess(TransferRequest transaction) {
        return Try.of(() -> {
                    handleSuccess(transaction);
                    return true;
                }).toEither()
                .mapLeft(e -> new BusinessError("Error notifying Kafka: " + e.getMessage()));
    }

    private void handleFailure(TransferRequest transaction) {
        transaction.setPocketUpdateStatus(PocketUpdateStatus.FAILED);
        objectMapper.tryToString(transaction)
                .map(msg -> kafkaTemplate.send(PocketConstant.POCKET_UPDATED_TOPIC, msg));
    }

    private void handleSuccess(TransferRequest transaction) {
        transaction.setPocketUpdateStatus(PocketUpdateStatus.SUCCESS);
        objectMapper.tryToString(transaction)
                .map(msg -> kafkaTemplate.send(PocketConstant.POCKET_UPDATED_TOPIC, msg));
    }
}
