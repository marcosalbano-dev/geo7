package org.geo7.helpers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String adminPassword = encoder.encode("admin123");
        String userPassword = encoder.encode("user123");

        System.out.println("Senha para admin@geo7.com: " + adminPassword);
        System.out.println("Senha para user@geo7.com: " + userPassword);

        // Testar se as senhas funcionam
        System.out.println("\nTestando senhas:");
        System.out.println("admin123 matches: " + encoder.matches("admin123", adminPassword));
        System.out.println("user123 matches: " + encoder.matches("user123", userPassword));
    }
}

