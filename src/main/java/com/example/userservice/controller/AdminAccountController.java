package com.example.userservice.controller;

import com.example.userservice.dto.AccountDTO;
import com.example.userservice.dto.ApiResponse;
import com.example.userservice.enums.AccountStatus;
import com.example.userservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminAccountController {

    private final AccountService accountService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<AccountDTO>>> listAllAccounts(
            @PageableDefault(page = 0, size = 100) Pageable pageable
    ) {
        return ResponseEntity.ok(accountService.getAllAccounts(pageable));
    }

    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<AccountDTO>> changeAccountStatus (
            @RequestParam String accountNumber,
            @RequestParam AccountStatus accountStatus
            ) {
        return ResponseEntity.ok(accountService.changeAccountStatus(accountNumber, accountStatus));
    }

}
