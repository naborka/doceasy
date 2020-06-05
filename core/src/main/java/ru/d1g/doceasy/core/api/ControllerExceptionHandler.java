package ru.d1g.doceasy.core.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Requested element not found")
    @ExceptionHandler({EntityNotFoundException.class})
    protected void handleNotFound() {
    }
}
