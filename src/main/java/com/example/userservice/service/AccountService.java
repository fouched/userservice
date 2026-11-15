package com.example.userservice.service;

import com.example.userservice.dto.AccountDTO;
import com.example.userservice.dto.ApiResponse;
import com.example.userservice.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

    ApiResponse<AccountDTO> getMyAccount();

    ApiResponse<AccountDTO> getAccountByNumber(String accountNumber);

    ApiResponse<AccountDTO> changeAccountStatus(String accountNumber, AccountStatus status);

    ApiResponse<Page<AccountDTO>> getAllAccounts(Pageable pageable);

}
