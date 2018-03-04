package com.tzak.app.helpers;

import com.tzak.app.utils.ServerChangeableUrls;
import com.tzak.app.utils.ServerDevToolsSettings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import sun.misc.URLClassPath;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ServerResourceHelper {

    public static String LOADER_PROPERTIES_NAME = "loader.properties";
    public static List<URL> reloadableUrls = new ArrayList<>();

    static {
        initReloadableUrls();
    }

    public static String getResourcePathByFileName(String resourceName) {
        URL sqlScriptUrl2 = ClassLoader.getSystemResource(resourceName);
//        String getPath = ClassLoader.getSystemResource(resourceName).getPath();
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

    //TODO do dokończenia - znalezienie wartosci propertki przeszukując wszystkie resource
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

    public static List<String> getResourcesDirs() {
        List<String> result = new ArrayList<>();
        for (URL dir: getResourcesURLsList()) {
            result.add(dir.getPath());
        }
        return result;
    }

    public static List<URL> getResourcesURLsList(){
        List<URL> urls = new ArrayList<URL>();
        URLClassLoader urlClassLoader = (URLClassLoader) URLClassLoader.getSystemClassLoader();// getSystemClassLoader();
        if(urlClassLoader!=null) {
            for (URL url : urlClassLoader.getURLs()) {
                urls.add(url);
                urls.addAll(ServerChangeableUrls.getUrlsFromClassPathOfJarManifestIfPossible(url));
            }
        } else {
            //TODO - do przemyślenia co pomocnego można z tego wyciągnąć - obecnie niepotrzebne
            URLClassPath urlClassPath2 = sun.misc.Launcher.getBootstrapClassPath();
        }
        return urls;
    }

    public static List<String> getReloadableUrlsStringList() {
        List<String> result = new ArrayList<>();
        for (URL dir: getReloadableUrls()) {
            result.add(dir.getPath());
        }
        return result;
    }

    //TODO wszystki pliki resource - nie tylko katalogi w których są
    public static List<String> getResourcesFilesPathsAndNamesNoJarsCheck(List<URL> urls) {
        List<String> result = new ArrayList<>();
        for (URL url: urls) {
            File folder = null;
                folder = CommonHelper.getFileFromURL(url);
                File[] listOfFiles = folder.listFiles();
                if(listOfFiles!=null) {
                    for (File file : listOfFiles) {
                        result.add(file.getAbsolutePath());
                    }
                }
        }
        return  result;
    }

    public static List<URL> getReloadableUrls(){
        return reloadableUrls;
    }

    private static void initReloadableUrls() {
        ServerDevToolsSettings settings = ServerDevToolsSettings.get();
        for (URL url : getResourcesURLsList()) {
            if ((settings.isRestartInclude(url) || ServerChangeableUrls.isFolderUrl(url.toString()))
                    && !settings.isRestartExclude(url)) {
                reloadableUrls.add(url);
            }
        }
    }
}
