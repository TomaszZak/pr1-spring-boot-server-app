package com.tzak.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class ServerTestController {

    @RequestMapping(value = "/servertest", method = RequestMethod.GET)
    public String servertest() {
        return this.getClass().getSimpleName() + "-> servertest !!";
    }
}