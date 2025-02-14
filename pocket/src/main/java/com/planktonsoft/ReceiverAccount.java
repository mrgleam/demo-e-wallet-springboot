package com.planktonsoft;

import lombok.*;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class ReceiverAccount extends Pocket {
    public ReceiverAccount(Pocket pocket) {
        BeanUtils.copyProperties(pocket, this);
    }
}
