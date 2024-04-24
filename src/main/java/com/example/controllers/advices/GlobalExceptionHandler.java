package com.example.controllers.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {ResponseStatusException.class})
    public ModelAndView handleResponseStatusException(ResponseStatusException e) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("404");
            return modelAndView;
        }

        throw e;
    }

    @ExceptionHandler(value = {Exception.class})
    public ModelAndView defaultErrorHandler(Exception e) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        return modelAndView;
    }
}