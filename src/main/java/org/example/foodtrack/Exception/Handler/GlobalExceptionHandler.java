package org.example.foodtrack.Exception.Handler;
import org.example.foodtrack.Dto.Response.FoodDiaryResponse;
import org.example.foodtrack.Exception.BadRequestException;
import org.example.foodtrack.Exception.ConflictException;
import org.example.foodtrack.Exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<FoodDiaryResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new FoodDiaryResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<FoodDiaryResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new FoodDiaryResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<FoodDiaryResponse> handleNotFound(ConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new FoodDiaryResponse(ex.getMessage(), HttpStatus.CONFLICT.value()));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<FoodDiaryResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new FoodDiaryResponse(
                        "Something went wrong : "+ ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<FoodDiaryResponse> handleForbidden(ForbiddenException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new FoodDiaryResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
    }
}
