package org.example.foodtrack.Service;

import lombok.RequiredArgsConstructor;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
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

            userRepository.save(user);

            return new FoodDiaryResponse(
                    "User registered successfully",
                    HttpStatus.CREATED.value()
            );
        } catch (Exception ex) {
            throw new BadRequestException("Error in userRegister Impl"+ex.getMessage());
        }

    }

    public FoodDiaryResponse userLogin(LoginRequest loginRequest) {
        try {
            Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
            if (user.isEmpty()) {
                throw new NotFoundException("User not Founded");
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
                throw new BadRequestException("Wrong Password");
            }

            String token = jwtUtil.generateToken(user.get().getEmail());
            return new FoodDiaryResponse(token,"User Login Successfully",HttpStatus.OK.value());
        } catch (Exception ex) {
            throw new BadRequestException("Error in UserLongin Impl"+ex.getMessage());
        }

    }
}
