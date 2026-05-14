package com.heim.api.fcm.application.dto;

import com.heim.api.fcm.domain.entity.FcmToken;
import lombok.Data;

@Data
public class FcmTokenRequest {
    private String token;
    private Long ownerId;
    private FcmToken.OwnerType ownerType;
}
