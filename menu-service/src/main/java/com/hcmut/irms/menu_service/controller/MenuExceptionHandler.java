package com.hcmut.irms.menu_service.controller;

import com.hcmut.irms.menu_service.exception.MenuBadRequestException;
import com.hcmut.irms.menu_service.exception.MenuConflictException;
import com.hcmut.irms.menu_service.exception.MenuNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class MenuExceptionHandler {
    @ExceptionHandler(MenuBadRequestException.class)
    public void handleBadRequest(MenuBadRequestException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(MenuNotFoundException.class)
    public void handleNotFound(MenuNotFoundException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(MenuConflictException.class)
    public void handleConflict(MenuConflictException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.CONFLICT.value(), ex.getMessage());
    }
}
