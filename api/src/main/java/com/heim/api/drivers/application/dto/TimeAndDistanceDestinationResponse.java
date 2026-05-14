package com.heim.api.drivers.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeAndDistanceDestinationResponse {
    private String distanceToDestination;
    private String timeToDestination;
}
