package org.example.foodtrack.Controller;

import lombok.RequiredArgsConstructor;
import org.example.foodtrack.Dto.Request.CreateDiaryEntryRequest;
import org.example.foodtrack.Dto.Response.DiaryEntryResponse;
import org.example.foodtrack.Service.DiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/food-diary/diary")
public class DairyController {
    private final DiaryService diaryService;

    @PostMapping("/add")
    public ResponseEntity<DiaryEntryResponse> addDiaryEntry(
            @RequestBody CreateDiaryEntryRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        DiaryEntryResponse diaryEntryResponse = diaryService.addDiaryEntry(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(diaryEntryResponse);
    }

}
