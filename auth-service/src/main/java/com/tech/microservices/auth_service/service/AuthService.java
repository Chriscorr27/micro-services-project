package com.tech.microservices.auth_service.service;

import com.tech.microservices.auth_service.entity.User;
import com.tech.microservices.auth_service.repository.UserRepository;
import com.tech.microservices.dto.request.LoginRequest;
import com.tech.microservices.dto.request.RegisterRequest;
import com.tech.microservices.dto.response.AuthResponse;
import com.tech.microservices.dto.response.UserResponse;
import com.tech.microservices.exception.ApplicationException;
import com.tech.microservices.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private UserResponse getUserResponseFromUser(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public AuthResponse login(LoginRequest request) throws ApplicationException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new ApplicationException("Invalid email or password"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new ApplicationException("Invalid email or password");
        }
        return new AuthResponse(JWTUtil.generateToken(user.getEmail(), user.getId()),getUserResponseFromUser(user));
    }

    public AuthResponse register(RegisterRequest request) throws ApplicationException {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw  new ApplicationException("Email already present");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return new AuthResponse(JWTUtil.generateToken(user.getEmail(), user.getId()),getUserResponseFromUser(user));
    }

    public User getUser(String email) throws ApplicationException {

        return userRepository.findByEmail(email)
                .orElseThrow(()->new ApplicationException("User not found."));
    }

    public User getUserById(Long id) throws ApplicationException {
        return userRepository.findById(id)
                .orElseThrow(()->new ApplicationException("User not found."));
    }
}
