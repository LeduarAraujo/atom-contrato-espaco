package com.atom.contratoespaco.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
public class TestController {

    @GetMapping
    public String test() {
        return "Backend funcionando!";
    }
}
