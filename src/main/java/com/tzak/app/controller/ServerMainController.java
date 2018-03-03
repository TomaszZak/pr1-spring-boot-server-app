package com.tzak.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class ServerMainController {

    @Autowired
    Environment env;

    @RequestMapping(value = "/servermain", method = RequestMethod.GET)
    public String serverMain() {
        return this.getClass().getSimpleName() + "-> Home !!";
    }

    @RequestMapping(value = "/environmentInfo", method = RequestMethod.GET)
    public String environmentInfo() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append("Environment Info:")
                .append("\n user.dir: " + System.getProperty("user.dir"))
                .append("\n Spring loader.path: " + env.getProperty("LOADER_PATH"))
        ;

        return stringBuilder.toString();
    }

}