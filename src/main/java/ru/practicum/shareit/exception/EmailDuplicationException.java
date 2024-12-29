package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j // Аннотация для автоматической генерации логгера
@ResponseStatus(HttpStatus.CONFLICT) // Устанавливаем статус 409 (конфликт) для этого исключения
public class EmailDuplicationException extends RuntimeException {
    public EmailDuplicationException(String message) {
        super(message);
        log.warn(message); // Логируем сообщение об ошибке на уровне WARN
    }
}