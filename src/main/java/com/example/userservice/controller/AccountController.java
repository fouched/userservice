package com.example.userservice.controller;

import com.example.userservice.dto.AccountDTO;
import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.UserAccountDTO;
import com.example.userservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AccountDTO>> profile() {
        return ResponseEntity.ok(accountService.getMyAccount());
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountDTO>> getByAccountNumber(
            @PathVariable String accountNumber
    ) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }

}
