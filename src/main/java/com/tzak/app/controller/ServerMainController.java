package com.tzak.app.controller;

import com.tzak.app.helpers.ServerResourceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.net.URLClassLoader;
import java.util.Arrays;

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
                        + ServerResourceHelper.getResourcePathByFileName(ServerResourceHelper.LOADER_PROPERTIES_NAME))
                .append("\n - loader.main : "
                        + ServerResourceHelper.getResourcePropertyByPropertyName(ServerResourceHelper.LOADER_PROPERTIES_NAME, "loader.main"))
                .append("\n - loader.path : "
                        + ServerResourceHelper.getResourcePropertyByPropertyName(ServerResourceHelper.LOADER_PROPERTIES_NAME, "loader.path"))
        ;
//        try {
            //TODO do dokończenia - nie znajduje loader.properties
//            List<Resource> resources = Arrays.asList(resourceResolver.getResources("classpath:*"));
//            EnvironmentHelper.findProperty(resources,"loader.path");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return stringBuilder.toString();
    }

    @RequestMapping(value = "/getResourcesDirs", method = RequestMethod.GET)
    public String getResourcesDirs() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String dir: ServerResourceHelper.getResourcesDirs()) {
            stringBuilder.append(dir + "\n");
        }
        return stringBuilder.toString();
    }

    @RequestMapping(value = "/getResourcesFilesPathsAndNamesNoJarsCheck", method = RequestMethod.GET)
    public String getResourcesFilesPathsAndNamesNoJarsCheck() {
        URLClassLoader urlClassLoader = (URLClassLoader) URLClassLoader.getSystemClassLoader();
        StringBuilder stringBuilder = new StringBuilder();
        for (String dir: ServerResourceHelper.getResourcesFilesPathsAndNamesNoJarsCheck(Arrays.asList(urlClassLoader.getURLs()))) {
            stringBuilder.append(dir + "\n");
        }
        return stringBuilder.toString();
    }

    @RequestMapping(value = "/getReloadableUrls", method = RequestMethod.GET)
    public String getReloadableUrls() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String dir: ServerResourceHelper.getReloadableUrlsStringList()) {
            stringBuilder.append(dir + "\n");
        }
        return stringBuilder.toString();
    }

    @RequestMapping(value = "/getReloadableResourcesFiles", method = RequestMethod.GET)
    public String getReloadableResourcesFiles() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String dir: ServerResourceHelper.getResourcesFilesPathsAndNamesNoJarsCheck(ServerResourceHelper.getReloadableUrls())) {
            stringBuilder.append(dir + "\n");
            //TODO - tutaj dołożyć listę plików
        }
        return stringBuilder.toString();
    }

}