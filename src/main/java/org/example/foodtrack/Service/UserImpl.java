package org.example.foodtrack.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtrack.Dto.Request.LoginRequest;
import org.example.foodtrack.Dto.Request.RegisterRequest;
import org.example.foodtrack.Dto.Response.FoodDiaryResponse;
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

    public FoodDiaryResponse userRegister(RegisterRequest registerRequest) {
        try {
            if(registerRequest.getEmail() == null || registerRequest.getEmail().isBlank()) {
                throw new BadRequestException("Email is required");
            }

            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                throw new ConflictException("Email already exists");
            }

            User user = new User();
            user.setName(registerRequest.getName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setIsPro(false);
            userRepository.save(user);

            return new FoodDiaryResponse(
                    "User registered successfully",
                    HttpStatus.CREATED.value()
            );
        } catch (Exception ex) {
            throw new BadRequestException("Error in userRegister Impl");
        }

    }

    public FoodDiaryResponse userLogin(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.info("Wrong password");
            throw new BadRequestException("Wrong password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new FoodDiaryResponse(
                "User Login Successfully",
                HttpStatus.OK.value(),
                token
        );
    }

}
