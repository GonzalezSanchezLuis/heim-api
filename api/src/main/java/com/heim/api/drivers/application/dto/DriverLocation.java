package com.heim.api.drivers.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocation {
    private Long driverId;
    private double latitude;
    private double longitude;
}
