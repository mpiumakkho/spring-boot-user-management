package com.mp.web.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global exception handler for web controllers.
 * Centralizes error handling and logging.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger LOG = LogManager.getLogger(GlobalExceptionHandler.class);

    // จัดการ error สำหรับหน้าแสดงผล - แสดง error message บนหน้าเว็บ
    @ExceptionHandler(value = {WebApiException.class})
    public String handleWebApiError(WebApiException ex, Model model) {
        LOG.error("Web API error: {}", ex.getMessage(), ex);
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("currentPath", ex.getCurrentPath());
        return ex.getViewName();
    }

    // จัดการ error สำหรับการส่งฟอร์ม - redirect กลับพร้อม flash message
    @ExceptionHandler(value = {FormSubmissionException.class})
    public String handleFormError(FormSubmissionException ex, RedirectAttributes redirectAttributes) {
        LOG.error("Form submission error: {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:" + ex.getRedirectPath();
    }

    // จัดการ error ที่ไม่ได้คาดการณ์ไว้
    @ExceptionHandler(value = {Exception.class})
    public String handleUnexpectedError(Exception ex, Model model) {
        LOG.error("Unexpected error: {}", ex.getMessage(), ex);
        model.addAttribute("error", "An unexpected error occurred. Please try again later.");
        return "error";
    }
} 