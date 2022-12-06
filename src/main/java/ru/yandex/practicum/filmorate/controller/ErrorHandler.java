package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.controller.exception.FriendConfirmationException;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationError(ValidationException validationException) {
        log.warn(validationException.getMessage());
        return new ErrorResponse(validationException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(EntityNotFoundException entityNotFoundException) {
        log.warn(entityNotFoundException.getMessage());
        return new ErrorResponse(entityNotFoundException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFriendConfirmation(FriendConfirmationException friendConfirmationException) {
        log.warn(friendConfirmationException.getMessage());
        return new ErrorResponse(friendConfirmationException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerError(Exception exception) {
        log.warn("500", exception);
        return new ErrorResponse(exception.getMessage());
    }
}
