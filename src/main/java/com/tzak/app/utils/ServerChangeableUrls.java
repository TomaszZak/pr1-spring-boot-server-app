package com.tzak.app.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Włąsna implementacja org.springframework.boot.devtools.restart.ChangeableUrls
 */
public class ServerChangeableUrls {

    private static final Log logger = LogFactory.getLog(ServerChangeableUrls.class);

    private final List<URL> urls;

    private ServerChangeableUrls(URL... urls) {
        ServerDevToolsSettings settings = ServerDevToolsSettings.get();
        List<URL> reloadableUrls = new ArrayList<URL>(urls.length);
        for (URL url : urls) {
            if ((settings.isRestartInclude(url) || isFolderUrl(url.toString()))
                    && !settings.isRestartExclude(url)) {
                reloadableUrls.add(url);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Matching URLs for reloading : " + reloadableUrls);
        }
        this.urls = Collections.unmodifiableList(reloadableUrls);
    }

    public static boolean isFolderUrl(String urlString) {
        return urlString.startsWith("file:") && urlString.endsWith("/");
    }

    private static JarFile getJarFileIfPossible(URL url) {
        try {
            File file = new File(url.toURI());
            if (file.isFile()) {
                return new JarFile(file);
            }
        }
        catch (Exception ex) {
            // Assume it's not a jar and continue
        }
        return null;
    }

    public static List<URL> getUrlsFromClassPathOfJarManifestIfPossible(URL url) {
        JarFile jarFile = getJarFileIfPossible(url);
        if (jarFile == null) {
            return Collections.<URL>emptyList();
        }
        try {
            return getUrlsFromManifestClassPathAttribute(jarFile);
        }
        catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed to read Class-Path attribute from manifest of jar " + url,
                    ex);
        }
    }

    public static ServerChangeableUrls fromUrlClassLoader(URLClassLoader classLoader) {
        List<URL> urls = new ArrayList<URL>();
        for (URL url : classLoader.getURLs()) {
            urls.add(url);
            urls.addAll(getUrlsFromClassPathOfJarManifestIfPossible(url));
        }
        return fromUrls(urls);
    }

    private static List<URL> getUrlsFromManifestClassPathAttribute(JarFile jarFile)
            throws IOException {
        Manifest manifest = jarFile.getManifest();
        if (manifest == null) {
            return Collections.<URL>emptyList();
        }
        String classPath = manifest.getMainAttributes()
                .getValue(Attributes.Name.CLASS_PATH);
        if (!StringUtils.hasText(classPath)) {
            return Collections.emptyList();
        }
        String[] entries = StringUtils.delimitedListToStringArray(classPath, " ");
        List<URL> urls = new ArrayList<URL>(entries.length);
        File parent = new File(jarFile.getName()).getParentFile();
        for (String entry : entries) {
            try {
                File referenced = new File(parent, entry);
                if (referenced.exists()) {
                    urls.add(referenced.toURI().toURL());
                }
                else {
                    System.err.println("Ignoring Class-Path entry " + entry + " found in"
                            + jarFile.getName() + " as " + referenced
                            + " does not exist");
                }
            }
            catch (MalformedURLException ex) {
                throw new IllegalStateException(
                        "Class-Path attribute contains malformed URL", ex);
            }
        }
        return urls;
    }

    public static ServerChangeableUrls fromUrls(Collection<URL> urls) {
        return fromUrls(new ArrayList<URL>(urls).toArray(new URL[urls.size()]));
    }
    public static ServerChangeableUrls fromUrls(URL... urls) {
        return new ServerChangeableUrls(urls);
    }
}
