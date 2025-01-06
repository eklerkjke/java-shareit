package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j // Аннотация для автоматической генерации логгера
@ResponseStatus(HttpStatus.BAD_REQUEST) // Устанавливаем статус 404 (Не найдено) для этого исключения
public class UnAvaliableException extends RuntimeException {
        public UnAvaliableException(String message) {
            super(message);
            log.warn(message); // Логируем сообщение об ошибке на уровне WARN
        }
}