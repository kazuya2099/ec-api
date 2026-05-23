package com.example.ecapi.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * グローバル例外ハンドラー
 *
 * <p>API で発生した例外を適切な HTTP ステータスと JSON レスポンスに統一して変換する。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** バリデーションエラー → 400 Bad Request */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        error -> {
                            String field = ((FieldError) error).getField();
                            errors.put(field, error.getDefaultMessage());
                        });
        return buildError(HttpStatus.BAD_REQUEST, "バリデーションエラー", errors);
    }

    /** リソース未検出 → 404 Not Found */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(IllegalArgumentException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    /** 在庫不足などビジネスルール違反 → 409 Conflict */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(IllegalStateException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    /** その他サーバーエラー → 500 Internal Server Error */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Unexpected exception occurred", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "サーバーエラーが発生しました", null);
    }

    private ResponseEntity<Map<String, Object>> buildError(
            HttpStatus status, String message, Object detail) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", message);
        if (detail != null) {
            body.put("detail", detail);
        }
        return ResponseEntity.status(status).body(body);
    }
}
