package com.heim.api.users.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDataRequestDTO {
    private String fullName;
    private String email;
    private String password;
    private String  document;
    private String phone;
    private String urlAvatarProfile;
}
