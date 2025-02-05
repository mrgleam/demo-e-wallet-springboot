package com.planktonsoft;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String country;
    private String dob;

    @NotBlank
    private String identifierValue;

    @NotNull
    private UserIdentifier userIdentifier;

    public User to(){
        return User.builder()
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .password(this.password)
                .email(this.email)
                .userIdentifier(this.userIdentifier)
                .identifierValue(this.identifierValue)
                .build();
    }

}
