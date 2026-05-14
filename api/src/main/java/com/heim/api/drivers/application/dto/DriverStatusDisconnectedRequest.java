package com.heim.api.drivers.application.dto;

import lombok.Data;

@Data
public class DriverStatusDisconnectedRequest {
    private String status;
    private Long driverId;
}
