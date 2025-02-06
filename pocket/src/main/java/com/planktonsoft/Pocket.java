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
}
