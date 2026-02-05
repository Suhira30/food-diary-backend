package org.example.foodtrack.Controller;

import lombok.RequiredArgsConstructor;
import org.example.foodtrack.Dto.Request.LoginRequest;
import org.example.foodtrack.Dto.Request.RegisterRequest;
import org.example.foodtrack.Dto.Response.AuthResponse;
import org.example.foodtrack.Service.UserImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/food-diary")
public class AuthController {

    private final UserImpl userImpl;

    @GetMapping("/own_test")
    public String test() {
        System.out.println("test");
        return "test";
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse authResponse = userImpl.userRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse authResponse = userImpl.userLogin(request);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @PostMapping("/pro/register")
    public ResponseEntity<AuthResponse> proRegister(@RequestBody RegisterRequest request) {
        AuthResponse authResponse = userImpl.proUserRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }
}
