package com.example.userservice.service;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.UserAccountDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.dto.UserStatisticsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    ApiResponse<UserAccountDTO> getUserAccountDetails();
    ApiResponse<UserAccountDTO> search(String email, String accountNumber);
    ApiResponse<Page<UserDTO>> getAllUsers(String roleName, Pageable pageable);
    ApiResponse<UserStatisticsDTO> getUserStatistics();
    ApiResponse<String> toggleUserStatus(Long userId);
}
