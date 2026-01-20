package org.example.foodtrack.Controller;

import lombok.RequiredArgsConstructor;
import org.example.foodtrack.Dto.Response.FoodDiaryResponse;
import org.example.foodtrack.Dto.Request.RegisterRequest;
import org.example.foodtrack.Service.UserImpl;
import org.example.foodtrack.Util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/food-diary")
public class AuthController {

    private final UserImpl userImpl;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @GetMapping("/own_test")
    public String test() {
        System.out.println("test");
        return "test";
    }

    @PostMapping("/register")
    public ResponseEntity<FoodDiaryResponse> register(@RequestBody RegisterRequest request) {
        FoodDiaryResponse foodDiaryResponse=userImpl.userRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(foodDiaryResponse);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            return ResponseEntity.badRequest().body("Invalid password");
//        }
//
//        String token = jwtUtil.generateToken(user.getEmail());
//        return ResponseEntity.ok(token);
//    }


}
