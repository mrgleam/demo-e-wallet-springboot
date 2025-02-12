package com.planktonsoft;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pocket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Long userId;

    @Column(unique = true)
    private String phoneNumber;

    private Double balance;

    private String identifierValue;

    @Enumerated(value = EnumType.STRING)
    private UserIdentifier userIdentifier;

    public static Pocket from(UserMsg user){
        return Pocket.builder()
                .userId(user.getUserId())
                .phoneNumber(user.getPhoneNumber())
                .userIdentifier(user.getUserIdentifier())
                .identifierValue(user.getIdentifierValue())
                .balance(0.0)
                .build();
    }
}
