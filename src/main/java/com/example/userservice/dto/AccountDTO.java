package com.example.userservice.dto;

import com.example.userservice.entity.User;
import com.example.userservice.enums.AccountStatus;
import com.example.userservice.enums.AccountType;
import com.example.userservice.enums.Currency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {

    private Long id;

    private String accountNumber;

    private BigDecimal balance;

    private Currency currency;

    private AccountType accountType;

    private AccountStatus accountStatus;

    private UserDTO user;

    private LocalDateTime createdAt;
}
