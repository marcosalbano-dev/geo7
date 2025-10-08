package org.geo7.rest;

import org.geo7.dto.AuthResponseDTO;
import org.geo7.dto.LoginRequestDTO;
import org.geo7.dto.RegisterRequestDTO;
import org.geo7.dto.UserDTO;
import org.geo7.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        try {
            AuthResponseDTO response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        try {
            AuthResponseDTO response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // Extrair userId do token
            UUID userId = extractUserIdFromToken(token);
            UserDTO user = userService.getCurrentUser(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Com JWT, o logout é feito no frontend removendo o token
        return ResponseEntity.ok("Logout realizado com sucesso");
    }

    private UUID extractUserIdFromToken(String token) {
        // Remover "Bearer " do início do token
        String cleanToken = token.replace("Bearer ", "");

        // Aqui você pode usar o JwtService para extrair o userId
        // Por enquanto, vou usar um método simples
        try {
            // Em produção, use o JwtService para validar e extrair o userId
            // return UUID.fromString(jwtService.extractUserId(cleanToken));

            // Para teste, retornando um UUID fixo
            // TODO: Implementar extração real do token
            return UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        } catch (Exception e) {
            throw new RuntimeException("Token inválido");
        }
    }
}

