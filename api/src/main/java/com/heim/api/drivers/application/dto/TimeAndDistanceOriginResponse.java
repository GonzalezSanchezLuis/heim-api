package com.heim.api.drivers.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeAndDistanceOriginResponse {
    private String distance;
    private String estimatedTimeOfArrival;

}
