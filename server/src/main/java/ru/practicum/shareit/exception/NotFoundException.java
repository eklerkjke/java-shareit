package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j // Аннотация для автоматической генерации логгера
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
        log.warn(message); // Логируем сообщение об ошибке на уровне WARN
    }
}