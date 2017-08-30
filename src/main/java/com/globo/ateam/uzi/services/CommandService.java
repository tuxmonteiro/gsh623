package com.globo.ateam.uzi.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class CommandService {

    private static final String COMM_PREFIX = System.getProperty("uzi.command", "/bin/true");

    public BufferedReader run(long id) throws IOException {
        Process process = new ProcessBuilder(COMM_PREFIX).start();
        InputStream is = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(is);
        return new BufferedReader(inputStreamReader);
    }
}
