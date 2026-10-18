package com.example.userservice.service;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.RegistrationRequest;

public interface AuthService {

    ApiResponse<AuthResponse> register(RegistrationRequest e);

    ApiResponse<AuthResponse> login(RegistrationRequest e);
}
