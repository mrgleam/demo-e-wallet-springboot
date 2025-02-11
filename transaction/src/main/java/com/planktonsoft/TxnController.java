package com.planktonsoft;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TxnController {

    private final TxnService txnService;

    @PostMapping("/txn")
    public String initiateTxn(@RequestParam("receiver") String receiver,
                              @RequestParam("purpose") String purpose,
                              @RequestParam("amount") Double amount) throws JsonProcessingException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return txnService.initiateTxn(authentication.getName(), receiver, purpose, amount);
    }
}
