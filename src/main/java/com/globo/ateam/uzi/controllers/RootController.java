package com.globo.ateam.uzi.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/")
public class RootController {

    @Value("${build.project")
    private String buildProject;

    @Value("${build.version}")
    private String buildVersion;

    @Value("${build.timestamp}")
    private String buildTimestamp;

    @GetMapping
    public String get() {
        return String.format("{\"name\":\"uzi\", \"version\":\"%s\", \"build\":\"%s\"}", buildVersion, buildTimestamp);
    }

}
