package com.example.userservice.service.impl;

import com.example.userservice.dto.*;
import com.example.userservice.entity.Account;
import com.example.userservice.entity.Role;
import com.example.userservice.entity.User;
import com.example.userservice.enums.AccountStatus;
import com.example.userservice.enums.AccountType;
import com.example.userservice.enums.Currency;
import com.example.userservice.exceptions.BadRequestException;
import com.example.userservice.kafka.dto.UserRegistrationEvent;
import com.example.userservice.kafka.service.AccountEventPublisher;
import com.example.userservice.repository.AccountRepository;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtService;
import com.example.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final AccountEventPublisher accountEventPublisher;

    @Override
    public ApiResponse<AuthResponse> register(RegistrationRequest registrationRequest) {
        log.info("Received request to register a new user");
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        Set<Role> roles = new HashSet<>();

        String roleName = (registrationRequest.getRole() != null && registrationRequest.getRole().isBlank())
                ? registrationRequest.getRole().toUpperCase()
                : "CUSTOMER";

        Role databaseRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Role not found"));

        roles.add(databaseRole);

        User user = User.builder()
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .enabled(true)
                .roles(roles)
                .build();


        userRepository.save(user);

        // generate unique account number
        String accountNumber = generateUniqueAccountNumber();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .currency(Currency.ZAR)
                .accountType(AccountType.SAVINGS) // this does not seem right?
                .accountStatus(AccountStatus.ACTIVE)
                .user(user)
                .build();

        accountRepository.save(account);

        // Publish user registration event to Notification Service via Kafka
        UserRegistrationEvent userRegistrationEvent = UserRegistrationEvent.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accountNumber(accountNumber)
                .bankName("Fintech Bank")
                .build();
        accountEventPublisher.publishUserRegistrationEvent(userRegistrationEvent);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        AuthResponse authResponse = AuthResponse.builder()
                .user(userDTO)
                .build();

        return new ApiResponse<>(HttpStatus.CREATED.value(), "User registered successfully", authResponse);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        do {
            int randomNumber = random.nextInt(100_000_000);
            accountNumber = String.format("00%08d", randomNumber);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    @Override
    public ApiResponse<AuthResponse> login(LoginRequest loginRequest) {
        log.info("Received request to login user with email: {}", loginRequest.getEmail());
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        if (!user.isEnabled()) {
            throw new BadRequestException("User account is disabled");
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String token = jwtService.generateToken(user.getEmail(), roles);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();

        return new ApiResponse<>(HttpStatus.OK.value(), "User logged in successfully", authResponse);
    }
}
