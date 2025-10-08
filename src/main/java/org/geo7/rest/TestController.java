package org.geo7.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:4200")
public class TestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/password")
    public String testPassword(@RequestParam String password) {
        String encoded = passwordEncoder.encode(password);
        boolean matches = passwordEncoder.matches(password, encoded);

        return String.format("Password: %s\nEncoded: %s\nMatches: %s",
                password, encoded, matches);
    }

    @GetMapping("/password/verify")
    public String verifyPassword(@RequestParam String password, @RequestParam String hash) {
        boolean matches = passwordEncoder.matches(password, hash);
        return String.format("Password: %s\nHash: %s\nMatches: %s",
                password, hash, matches);
    }
}

