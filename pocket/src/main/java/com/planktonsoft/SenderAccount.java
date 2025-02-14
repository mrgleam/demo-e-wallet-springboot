package com.planktonsoft;

import io.vavr.control.Either;
import lombok.*;
import org.springframework.beans.BeanUtils;

import static io.vavr.API.Left;
import static io.vavr.API.Right;

@Getter
@Setter
public class SenderAccount extends Pocket{
    public SenderAccount(Pocket pocket) {
        BeanUtils.copyProperties(pocket, this);
    }

    public Either<BusinessError, Boolean> hasSufficientFunds(Double amount) {
        if (this.getBalance() >= amount) {
            return Right(true);
        } else {
            return Left(new BusinessError("Sender's balance is not enough"));
        }
    }
}
