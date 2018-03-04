package com.tzak.app.helpers;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

public class CommonHelper {

    public static Thread getMainThread(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread thread: threadSet) {
            if(isMainThread(thread)) {
                return  thread;
            }
        }
        return  null;
    }

    protected static boolean isMainThread(Thread thread) {
        return thread.getName().equals("main") && thread.getContextClassLoader()
                .getClass().getName().contains("AppClassLoader");
    }

    public static File getFileFromURL(URL url) {
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        } finally {
            return file;
        }
    }
}
