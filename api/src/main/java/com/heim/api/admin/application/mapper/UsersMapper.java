package com.heim.api.admin.application.mapper;

import com.heim.api.admin.application.dto.DriverSummaryResponse;
import com.heim.api.admin.application.dto.UserResponse;
import com.heim.api.admin.application.dto.UserUpdateRequest;
import com.heim.api.drivers.domain.entity.Driver;
import com.heim.api.users.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsersMapper {
    @Mapping(source = "driver", target = "driverData")

    UserResponse toResponse(User user);

    DriverSummaryResponse toDriverResponse(Driver driver);

    User toEntity(UserUpdateRequest userUpdateRequest);
}
