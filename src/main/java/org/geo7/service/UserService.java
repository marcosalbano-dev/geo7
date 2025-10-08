package org.geo7.service;


import org.geo7.dto.AuthResponseDTO;
import org.geo7.dto.LoginRequestDTO;
import org.geo7.dto.RegisterRequestDTO;
import org.geo7.dto.UserDTO;
import org.geo7.model.entity.User;
import org.geo7.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Verificar se usuário já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Usuário já existe com este email");
        }

        // Criar novo usuário
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(request.getRole());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Gerar token
        String token = jwtService.generateToken(savedUser);

        return new AuthResponseDTO(
                "Usuário criado com sucesso",
                new UserDTO(savedUser),
                token
        );
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        System.out.println("=== DEBUG LOGIN ===");
        System.out.println("Email recebido: " + request.getEmail());
        System.out.println("Senha recebida: " + request.getPassword());

        // Buscar usuário
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            System.out.println("Usuário não encontrado no banco");
            throw new RuntimeException("Credenciais inválidas");
        }

        User user = userOpt.get();
        System.out.println("Usuário encontrado: " + user.getEmail());
        System.out.println("Usuário ativo: " + user.getActive());
        System.out.println("Senha no banco: " + user.getPassword());

        if (!user.getActive()) {
            System.out.println("Usuário inativo");
            throw new RuntimeException("Credenciais inválidas");
        }

        // Verificar senha
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("Senha confere: " + passwordMatches);

        if (!passwordMatches) {
            System.out.println("Senha não confere");
            throw new RuntimeException("Credenciais inválidas");
        }

        // Gerar token
        String token = jwtService.generateToken(user);
        System.out.println("Token gerado com sucesso");
        System.out.println("=== FIM DEBUG LOGIN ===");

        return new AuthResponseDTO(
                "Login realizado com sucesso",
                new UserDTO(user),
                token
        );
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        return new UserDTO(userOpt.get());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getActiveUsers() {
        return userRepository.findByActiveTrue().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(UUID userId, UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        User user = userOpt.get();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.getActive());

        User updatedUser = userRepository.save(user);
        return new UserDTO(updatedUser);
    }

    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuário não encontrado");
        }

        userRepository.deleteById(userId);
    }

    public UserDTO updateUserPassword(UUID userId, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));

        User updatedUser = userRepository.save(user);
        return new UserDTO(updatedUser);
    }
}