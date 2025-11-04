package com.example.TaskApp.exceptions;


import com.example.TaskApp.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)

        public ResponseEntity<Response<?>> handleAllUnknownException(Exception ex){
            Response<?> response = Response.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(ex.getMessage())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<Response<?>> handleNotFoundException(NotFoundException ex){
            Response<?> response = Response.builder()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(ex.getMessage())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<Response<?>> handleBadRequestException(BadRequestException ex){
            Response<?> response = Response.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message(ex.getMessage())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Response<?>> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining(", "));

        Response<?> response = Response.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed: " + errorMessage)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
