package org.example.foodtrack.Service;

import lombok.RequiredArgsConstructor;
import org.example.foodtrack.Dto.Request.RegisterRequest;
import org.example.foodtrack.Dto.Response.FoodDiaryResponse;
import org.example.foodtrack.Entity.User;
import org.example.foodtrack.Exception.BadRequestException;
import org.example.foodtrack.Exception.ConflictException;
import org.example.foodtrack.Repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public FoodDiaryResponse userRegister(RegisterRequest registerRequest) {

        if (registerRequest.getEmail() == null || registerRequest.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);

        return new FoodDiaryResponse(
                "User registered successfully",
                HttpStatus.CREATED.value()
        );

    }
}
