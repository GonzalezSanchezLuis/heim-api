package com.heim.api.users.application.mapper;

import com.heim.api.users.application.dto.UpdateUserDataRequestDTO;
import com.heim.api.users.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UpdateUserDataMapper {
    UpdateUserDataRequestDTO toResponse(User user);
    User toEntity(UpdateUserDataRequestDTO updateUserDataRequestDTO);
}
