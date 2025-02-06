package com.planktonsoft;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Transactional
public interface PocketRepository extends JpaRepository<Pocket, Integer> {

    Pocket findByPhoneNumber(String phoneNumber);

    @Modifying
    @Query("update Pocket w set w.balance = w.balance + ?2 where w.phoneNumber = ?1")
    void updatePocket(String phoneNumber, Double amount);

}
