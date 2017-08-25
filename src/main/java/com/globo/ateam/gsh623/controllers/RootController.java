package com.globo.ateam.gsh623.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RootController {

    @RequestMapping(value = "/test", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    public String get() {
        return "OK";
    }

}
