package com.heim.api.move.domain.enums;


public enum MoveStatus {
    SCHEDULED,  // Viaje programado para una fecha futura
    REQUESTED,  // Usuario solicita el viaje
    ASSIGNED,
    DRIVER_ARRIVED,
    MOVING_STARTED,
    MOVE_COMPLETE,
    CANCELLED
}
