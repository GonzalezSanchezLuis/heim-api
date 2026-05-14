package com.heim.api.drivers.domain.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DriverStatus {
    CONNECTED,   // Disponible para recibir solicitudes
    BUSY,        // Ocupado en un viaje
    DISCONNECTED // No disponible
}
