package com.planktonsoft;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.vavr.API.Right;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class SenderAccountTest {
    @Test
    void whenCreateSenderAccountWithValidBalance_thenValidateSuccess() {
        SenderAccount senderAccount = new SenderAccount(Pocket.builder().balance(100.0).build());

        assertEquals(Right(true), senderAccount.hasSufficientFunds(10.0));
    }
}
