package com.heim.api.webSocket.infraestructure.listener;

import com.heim.api.move.application.dto.MoveNotificationUserResponse;
import com.heim.api.move.domain.entity.Move;
import com.heim.api.payment.domain.DriverPaymentAccount;
import com.heim.api.payment.infraestructure.repository.DriverPaymentAccountRepository;
import com.heim.api.users.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoveNotificationUserFactory {

    private final DriverPaymentAccountRepository driverPaymentAccountRepository;

    public MoveNotificationUserResponse build(Move move) {
        User driverUser = move.getDriver().getUser();
        Long driverId = move.getDriver().getId();
        Long driverUserId = driverUser.getUserId();

        String accountNumber = driverPaymentAccountRepository.findByDriverId(driverId)
                .or(() -> driverPaymentAccountRepository.findByDriverId(driverUserId))
                .map(DriverPaymentAccount::getAccountNumber)
                .orElse("");

        return MoveNotificationUserResponse.builder()
                .driverId(driverId)
                .driverName(driverUser.getFullName())
                .driverPhone(driverUser.getPhone())
                .driverImageUrl(driverUser.getUrlAvatarProfile())
                .enrollVehicle(move.getDriver().getEnrollVehicle())
                .vehicleType(move.getDriver().getVehicleType())
                .moveId(move.getMoveId())
                .origin(move.getOrigin())
                .destination(move.getDestination())
                .accountNumber(accountNumber)
                .amount(move.getPrice() != null ? move.getPrice().toPlainString() : "")
                .build();
    }
}

