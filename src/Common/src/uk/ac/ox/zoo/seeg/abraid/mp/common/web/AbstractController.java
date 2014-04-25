package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * A base class for web mvc controllers.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractController {
    /**
     * Initializes request mapping bindings.
     * @param binder The web data binder.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(" \t\r\n\f", true));
    }
}
