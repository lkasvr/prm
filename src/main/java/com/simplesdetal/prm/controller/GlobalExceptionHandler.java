package com.simplesdetal.prm.controller;

import com.simplesdetal.prm.exception.NegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_MESSAGE = "Ocorreu um erro interno no servidor.";

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleException(Exception e) {
        if (e instanceof NegocioException) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        LOGGER.error(ERROR_MESSAGE, e);
        return new ResponseEntity<>(ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
