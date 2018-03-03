package com.tzak.app.controller;

import com.tzak.app.SpringBootServerAppApplication;
import com.tzak.app.helpers.EnvironmentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@RequestMapping("/api")
@RestController
public class ServerMainController {

    @Autowired
    private WebApplicationContext appContext;

    @Autowired
    ResourcePatternResolver resourceResolver;

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
                .append("\n File loader.properties dir: "
                        + EnvironmentHelper.getResourcePathByFileName(EnvironmentHelper.LOADER_PROPERTIES_NAME))
                .append("\n - loader.main : "
                        + EnvironmentHelper.getResourcePropertyByPropertyName(EnvironmentHelper.LOADER_PROPERTIES_NAME, "loader.main"))
                .append("\n - loader.path : "
                        + EnvironmentHelper.getResourcePropertyByPropertyName(EnvironmentHelper.LOADER_PROPERTIES_NAME, "loader.path"))

        ;

        try {
            //TODO do dokończenia - nie znajduje loader.properties
            List<Resource> resources = Arrays.asList(resourceResolver.getResources("classpath:*"));
            EnvironmentHelper.findProperty(resources,"loader.path");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

}