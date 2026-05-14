package com.heim.api.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverSummaryResponse {
    private String licenseNumber;
    private String licenseCategory;
    private String vehicleType;
    private String enrollVehicle;
    private String status;
}
