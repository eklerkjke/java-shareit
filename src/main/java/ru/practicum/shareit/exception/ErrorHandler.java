package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // Аннотация, указывающая, что класс будет обрабатывать исключения в REST-контроллерах
public class ErrorHandler {

    @ExceptionHandler // Указывает, что этот метод будет вызываться при возникновении MethodArgumentNotValidException
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Устанавливает статус ответа 400 (BAD REQUEST)
    // Метод для обработки исключений валидации аргументов методов
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        // Разделение сообщения об ошибке для извлечения причин невалидности
        String[] messageArray = exception.getMessage().split("default message \\[");
        // Создание объекта StringBuilder для формирования сообщения об ошибке
        StringBuilder message = new StringBuilder();
        for (int index = 2; index < messageArray.length; index++) { // Начинаем с 2, чтобы пропустить первые два элемента
            message.append(messageArray[index].split("]")[0]); // Извлечение сообщения об ошибке
            message.append(". "); // Добавление точки после каждого сообщения
        }
        return new ErrorResponse(message.toString()); // Возвращение сформированного сообщения об ошибке
    }

    @ExceptionHandler // Указывает, что этот метод будет вызываться при возникновении NotFoundException
    @ResponseStatus(HttpStatus.NOT_FOUND) // Устанавливает статус ответа 404 (NOT FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException exception) {
        return new ErrorResponse(exception.getMessage()); // Возвращает сообщение об ошибке из исключения
    }

    @ExceptionHandler // Указывает, что этот метод будет вызываться при возникновении EmailDuplicationException
    @ResponseStatus(HttpStatus.CONFLICT) // Устанавливает статус ответа 409 (CONFLICT)
    public ErrorResponse handleEmailDuplicationException(final EmailDuplicationException exception) {
        return new ErrorResponse(exception.getMessage()); // Возвращает сообщение об ошибке из исключения
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException exception) {
        return new ErrorResponse(exception.getMessage());
    }
}