package com.school.portal.security.service;

import com.school.portal.security.JwtService;
import com.school.portal.security.dto.AuthResponse;
import com.school.portal.security.dto.LoginRequest;
import com.school.portal.security.dto.RefreshRequest;
import com.school.portal.user.User;
import com.school.portal.user.UserRepository;
import com.school.portal.user.dto.UserDto;
import com.school.portal.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String refreshToken = request.refreshToken();
        String email = jwtService.parseToken(refreshToken).getSubject();
        User user = userRepository.findByEmail(email).orElseThrow();
        return buildAuthResponse(user, refreshToken);
    }

    public UserDto me(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDto).orElseThrow();
    }

    private AuthResponse buildAuthResponse(User user) {
        return buildAuthResponse(user, jwtService.generateRefreshToken(user));
    }

    private AuthResponse buildAuthResponse(User user, String refreshToken) {
        String accessToken = jwtService.generateAccessToken(user);
        UserDto dto = userMapper.toDto(user);
        return new AuthResponse(accessToken, refreshToken, dto);
    }
}
