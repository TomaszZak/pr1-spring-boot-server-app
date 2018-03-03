package com.tzak.app.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class EnvironmentHelper {

    public static String LOADER_PROPERTIES_NAME = "loader.properties";

    public static String getResourcePathByFileName(String resourceName) {
        URL sqlScriptUrl2 = ClassLoader.getSystemResource(resourceName);
        String resourceDontExist = "ERROR - Resource: " + resourceName + " nie istnieje!!!!";
        return (sqlScriptUrl2 == null) ? resourceDontExist : sqlScriptUrl2.toString();
    }

    public static String getResourcePropertyByPropertyName(String resourceName, String propertyName) {
        Resource resource = new ClassPathResource(resourceName);
        String result = "";
        String resourceDontExist = "Resource: " + resourceName + " nie istnieje!!!!";
        try {
            if(resource!=null) {
                Properties props = PropertiesLoaderUtils.loadProperties(resource);
                result = Optional.ofNullable(props.getProperty(propertyName))
                        .orElse(resourceDontExist);
            } else result = resourceDontExist;
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
        return result;
    }

    public static String findProperty(List<Resource> resources, String propertyName) {

        try {
            for (Resource resource : resources) {
                Properties props = PropertiesLoaderUtils.loadProperties(resource);
                if("loader.properties".equals(resource.getFilename())) {
                    if (props.containsKey("loader.path")) {
                        return props.getProperty("loader.path");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
