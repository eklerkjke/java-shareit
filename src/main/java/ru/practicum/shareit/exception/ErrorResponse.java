package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter // Генерирует геттер для поля error
@AllArgsConstructor
public class ErrorResponse {
    private final String error; // Поле для хранения сообщения об ошибке
}