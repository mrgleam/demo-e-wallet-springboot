package com.planktonsoft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public void create(UserCreateRequest userCreateRequest) throws JsonProcessingException {
        User user = userCreateRequest.to();
        user.setPassword(encryptPwd(user.getPassword()));
        user.setAuthorities(UserConstant.USER_AUTHORITY);

        // TODO: Save user to database

        // TODO: Publish the event post user creation which can be listened by consumers

        String USER_CREATION_TOPIC = "user_create";
        kafkaTemplate.send(USER_CREATION_TOPIC,
                objectMapper.writeValueAsString(userCreateRequest));
    }

    private String encryptPwd(String rawPwd){
        return passwordEncoder.encode(rawPwd);
    }
}
