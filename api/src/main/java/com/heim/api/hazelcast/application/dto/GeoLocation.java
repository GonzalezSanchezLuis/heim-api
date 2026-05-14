package com.heim.api.hazelcast.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoLocation {
    private double latitude;
    private double longitude;
}
