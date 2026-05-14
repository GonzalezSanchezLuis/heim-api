package com.heim.api.drivers.domain.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OwnerType {
    USER,
    DRIVER
}



