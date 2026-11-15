package com.example.userservice.service.impl;

import com.example.userservice.dto.*;
import com.example.userservice.entity.Account;
import com.example.userservice.entity.User;
import com.example.userservice.exceptions.BadRequestException;
import com.example.userservice.exceptions.NotFoundException;
import com.example.userservice.repository.AccountRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<UserAccountDTO> getUserAccountDetails() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("getUserAccountDetails user email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        UserAccountDTO userAccountDTO = mapAccountToUser(user, account);
        return new ApiResponse<>(HttpStatus.OK.value(), "profile retrieved", userAccountDTO);
    }

    private UserAccountDTO mapAccountToUser(User user, Account account) {
        return UserAccountDTO.builder()
                .user(modelMapper.map(user, UserDTO.class))
                .account(modelMapper.map(account, AccountDTO.class))
                .build();
    }

    @Override
    public ApiResponse<UserAccountDTO> search(String email, String accountNumber) {
        log.debug("search by email: {} or accountNumber: {}", email, accountNumber);
        User user;
        Account account;
        if (email != null && !email.isBlank()) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("user with email not found"));
            account = accountRepository.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("user account not found"));
        } else if (accountNumber != null && !accountNumber.isBlank()) {
            account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new NotFoundException("Account not found"));
            user = account.getUser();
        } else {
            throw new BadRequestException("Email or Account Number required");
        }

        UserAccountDTO userAccountDTO = mapAccountToUser(user, account);
        return new ApiResponse<>(HttpStatus.OK.value(), "profile retrieved", userAccountDTO);
    }

    @Override
    public ApiResponse<Page<UserDTO>> getAllUsers(String roleName, Pageable pageable) {
        log.debug("getAllUsers");

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending());

        Page<User> userPage;
        if (roleName != null && !roleName.isBlank()) {
            userPage = userRepository.findByRoleName(roleName.toUpperCase(), sortedPageable);
        } else {
            userPage = userRepository.findAll(sortedPageable);
        }

        Page<UserDTO> dtoPage = userPage.map(user -> modelMapper.map(user, UserDTO.class));
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "users retrieved",
                dtoPage
        );
    }

    @Override
    public ApiResponse<UserStatisticsDTO> getUserStatistics() {
        log.debug("getUserStatistics");

        long total = userRepository.count();
        long enabled = userRepository.countByEnabledTrue();

        UserStatisticsDTO statisticsDTO = UserStatisticsDTO.builder()
                .totalUsers(total)
                .activeUsers(enabled)
                .inactiveUsers(total - enabled)
                .totalAccounts(accountRepository.count())
                .customersCount(userRepository.countByRoleName("CUSTOMER"))
                .adminsCount(userRepository.countByRoleName("ADMIN"))
                .build();

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "statistics retrieved",
                statisticsDTO
        );
    }

    @Override
    public ApiResponse<String> toggleUserStatus(Long userId) {
        log.debug("toggleUserStatus");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

       user.setEnabled(!user.isEnabled());
       userRepository.save(user);

       String status = user.isEnabled() ? "Enabled" : "Disabled";
       log.info("User with ID {} set to status {}", userId, user.isEnabled());

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "user status changed to: " + user.isEnabled(),
                null
        );
    }
}
