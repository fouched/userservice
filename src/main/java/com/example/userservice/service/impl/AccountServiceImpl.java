package com.example.userservice.service.impl;

import com.example.userservice.dto.AccountDTO;
import com.example.userservice.dto.ApiResponse;
import com.example.userservice.entity.Account;
import com.example.userservice.entity.User;
import com.example.userservice.enums.AccountStatus;
import com.example.userservice.exceptions.NotFoundException;
import com.example.userservice.repository.AccountRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.SecurityUtils;
import com.example.userservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<AccountDTO> getMyAccount() {
        log.debug("Getting account for logged in user");

        String email = SecurityUtils.currentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);
        accountDTO.setOwnerEmail(account.getUser().getEmail());

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account retrieved",
                accountDTO
        );
    }

    @Override
    public ApiResponse<AccountDTO> getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);
        accountDTO.setOwnerEmail(account.getUser().getEmail());

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account retrieved",
                accountDTO
        );
    }

    @Override
    public ApiResponse<AccountDTO> changeAccountStatus(String accountNumber, AccountStatus status) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        account.setAccountStatus(status);

        Account savedAccount = accountRepository.save(account);
        AccountDTO accountDTO = modelMapper.map(savedAccount, AccountDTO.class);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account status updated to: " + status.name(),
                accountDTO
        );
    }

    @Override
    public ApiResponse<Page<AccountDTO>> getAllAccounts(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );

        Page<Account> accounts = accountRepository.findAll(sortedPageable);
        Page<AccountDTO> dtoPage = accounts.map(account -> modelMapper.map(account, AccountDTO.class));

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Accounts retrieved",
                dtoPage
        );
    }
}
