package com.heim.api.drivers.application.dto;

import lombok.Data;

@Data
public class DriverStatusRequest {
    private String status;
    private Double latitude;
    private Double longitude;
}
