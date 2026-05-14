package com.heim.api.users.infraestructure.jwt;

import lombok.Data;

@Data
public class JwtRequest {
    private String email;
    private String password;
}
