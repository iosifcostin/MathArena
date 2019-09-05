package iosifcostin.MathArena.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ControllerAdviseInterceptor {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxSizeException(
            MaxUploadSizeExceededException exc,
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView modelAndView = new ModelAndView("errors/errors");
        modelAndView.getModel().put("message", "Marimea fisierului depaseste 5mb! ... ");
        return modelAndView;
    }

    @ExceptionHandler(NumberFormatException.class)
    public ModelAndView handleNumberFormatException(
            NumberFormatException exc,
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView modelAndView = new ModelAndView("errors/errors");
        modelAndView.getModel().put("message", " Valoarea introdusa trebuie sa fie un numar intreg ... ");
        return modelAndView;
    }


//    @ExceptionHandler(AccountStatusException.class)
//    public ModelAndView handleDisabledException(
//            AccountStatusException exc,
//            HttpServletRequest request,
//            HttpServletResponse response) {
//
//        ModelAndView modelAndView = new ModelAndView("errors/500");
//        modelAndView.getModel().put("message", "Max Upload Size Exceeded! ...  MAxSize = 1MB");
//        return modelAndView;
//    }
}
