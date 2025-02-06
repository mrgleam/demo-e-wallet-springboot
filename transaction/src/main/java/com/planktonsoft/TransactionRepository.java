package com.planktonsoft;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Modifying
    @Query("update Transaction t set t.transactionStatus = ?2 where t.transactionId = ?1")
    void updateTxn(String txnId, TransactionStatus transactionStatus);
}
