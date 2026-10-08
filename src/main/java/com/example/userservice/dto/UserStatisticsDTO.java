package com.example.userservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticsDTO {
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long totalAccounts;
    private Long averageAccountsPerUser;
    private Long customersCount;
    private Long adminsCount;

}
