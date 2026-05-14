package com.heim.api.users.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class UserRegistrationResponse {
    private UserResponse userResponse;
    private String token;
}
