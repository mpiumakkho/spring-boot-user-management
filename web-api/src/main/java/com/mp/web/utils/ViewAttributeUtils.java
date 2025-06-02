package com.mp.web.utils;

import org.springframework.ui.Model;

/**
 * Helper methods for setting common view attributes.
 * Used to set current page path and error messages in views.
 */
public final class ViewAttributeUtils {
    
    private ViewAttributeUtils() {
        throw new AssertionError("Utility class - do not instantiate");
    }
    
    /**
     * Sets current path in the view model
     */
    public static void addCommonAttributes(Model model, String currentPath) {
        model.addAttribute("currentPath", currentPath);
    }
    
    /**
     * Sets current path and error message in the view model
     */
    public static void addCommonAttributes(Model model, String currentPath, String error) {
        addCommonAttributes(model, currentPath);
        if (error != null) {
            model.addAttribute("error", error);
        }
    }
} 