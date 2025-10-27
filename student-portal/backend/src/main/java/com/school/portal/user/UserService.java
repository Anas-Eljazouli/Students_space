package com.school.portal.user;

import com.school.portal.common.RoleType;
import com.school.portal.user.dto.UserDto;
import com.school.portal.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public List<UserDto> findAllByRole(RoleType role) {
        return userRepository.findAllByRole(role).stream().map(userMapper::toDto).toList();
    }
}
