package com.school.portal.user.mapper;

import com.school.portal.user.User;
import com.school.portal.user.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
