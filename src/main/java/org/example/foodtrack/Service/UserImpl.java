package org.example.foodtrack.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtrack.Dto.Request.LoginRequest;
import org.example.foodtrack.Dto.Request.RegisterRequest;
import org.example.foodtrack.Dto.Response.AuthResponse;
import org.example.foodtrack.Dto.Response.UserInfo;
import org.example.foodtrack.Entity.User;
import org.example.foodtrack.Exception.BadRequestException;
import org.example.foodtrack.Exception.ConflictException;
import org.example.foodtrack.Exception.NotFoundException;
import org.example.foodtrack.Repo.UserRepository;
import org.example.foodtrack.Util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse userRegister(RegisterRequest registerRequest) {
            if(registerRequest.getEmail() == null || registerRequest.getEmail().isBlank()) {
                throw new BadRequestException("Email is required");
            }
            if (registerRequest.getName() == null || registerRequest.getName().isBlank()) {
                throw new BadRequestException("Name is required");
            }

            // Validate password
            if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
                throw new BadRequestException("Password must be at least 6 characters");
            }
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                throw new ConflictException("Email already exists");
            }

            User user = new User();
            user.setName(registerRequest.getName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setIsPro(true);
            userRepository.save(user);

            return new AuthResponse(
                    "User registered successfully. Please login.",
                    HttpStatus.CREATED.value()
            );

    }

    public AuthResponse userLogin(LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Failed login attempt for email: {}", loginRequest.getEmail());
            throw new BadRequestException("Wrong password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIsPro()
        );
        return new AuthResponse(
                "Login successful",
                HttpStatus.OK.value(),
                token,
                userInfo
        );
    }

}
