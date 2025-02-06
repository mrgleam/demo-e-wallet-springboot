package com.planktonsoft;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
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

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails user = (UserDetails) authentication.getPrincipal();

        return txnService.initiateTxn("+1234567890", receiver, purpose, amount);
    }
}
