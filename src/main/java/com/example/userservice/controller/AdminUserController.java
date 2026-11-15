package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.UserAccountDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.dto.UserStatisticsDTO;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserAccountDTO>> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String accountNumber
    ) {
        return ResponseEntity.ok(userService.search(email, accountNumber));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUser(
            @RequestParam(required = false) String roleName,
            @PageableDefault(page = 0, size = 100) Pageable pageable
            ) {
        return ResponseEntity.ok(userService.getAllUsers(roleName, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserStatisticsDTO>> getStatistics() {
        return ResponseEntity.ok(userService.getUserStatistics());
    }

    @PatchMapping("/toggle/{userId}")
    public ResponseEntity<ApiResponse<String>> toggle(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.toggleUserStatus(userId));
    }
}
