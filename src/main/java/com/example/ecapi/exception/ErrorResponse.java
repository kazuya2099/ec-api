package com.example.ecapi.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * API エラーレスポンス
 *
 * <p>全ての例外ハンドラーが統一してこの型を返す。 バリデーションエラー時は details にフィールドごとのエラー内容が入る。 それ以外の場合は details は null。
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> details) {

    /** details なし（通常エラー用） */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this(timestamp, status, error, message, null);
    }
}
