package com.heim.api.users.application.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String document;
    private String urlAvatarProfile;
    private String role;
    private String createdAt;
    private Boolean active;
    private String token;
    private Boolean hasFirstTripDiscount;


    public UserResponse(
            Long userId,
            String fullName,
            String email,
            String role,
            String phone,
            String urlAvatarProfile,
            boolean active,
            String token,
            boolean hasFirstTripDiscount,
            String document) {
    }

    public UserResponse(Long userId,
                        String fullName,
                        String email,
                        String phone,
                        String document,
                        String urlAvatarProfile,
                        String role,
                        String string,
                        boolean active,
                        String token,
                        boolean hasFirstTripDiscount,
                        Object o) {
    }
}
