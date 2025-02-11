package com.planktonsoft;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
        UserDetails userDetails = userService.loadUserByUsername(request.getPhoneNumber());
        System.out.println(userDetails);

        if (userService.verifyPwd(request.getPassword(), userDetails.getPassword())) {
            String token = userService.generateToken(request.getPhoneNumber());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}