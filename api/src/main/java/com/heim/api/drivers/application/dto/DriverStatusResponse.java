package com.heim.api.drivers.application.dto;

import com.heim.api.drivers.domain.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DriverStatusResponse {
    private final  Long id;
    private final DriverStatus status;


    public DriverStatusResponse(Long id, DriverStatus status) {
        this.id = id;
        this.status = status;
    }

    public Long getDriverId() {
        return id;
    }

    public DriverStatus getStatus() {
        return status;
    }
}
