package com.planktonsoft;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserConfig {

    @Bean
    PasswordEncoder getPE() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ObjectMapper getMapper(){
        return new ObjectMapper();
    }
}
