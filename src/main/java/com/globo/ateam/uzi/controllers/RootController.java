package com.globo.ateam.uzi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping(value = {"{.*}", ""})
    public String get() {
        return "OK";
    }

}
