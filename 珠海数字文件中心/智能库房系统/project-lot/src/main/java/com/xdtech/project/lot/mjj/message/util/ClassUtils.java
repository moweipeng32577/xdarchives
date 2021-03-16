package com.xdtech.project.lot.mjj.message.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("rawtypes")
public class ClassUtils {

    public static List<Class> getClassesFromPackage(String path) throws IOException, ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();

        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(path.replace('.', '/'));
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();

            if ("file".equals(protocol)) {
                classes.addAll(getClassesFromPackageByFile(path, new File(URLDecoder.decode(url.getFile(), "UTF-8"))));
            } else if ("jar".equals(protocol)) {

                String file = URLDecoder.decode(url.getFile(), "UTF-8");
                classes.addAll(getClassesFromPackageByJar(path, new File(file.substring(6, file.lastIndexOf('!')))));
            }
        }

        return classes;
    }

    public static List<Class> getClassesFromPackageByFile(String path, File dir) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();

        if (!dir.exists() || !dir.isDirectory()) {
            return classes;
        }

        File[] files = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".class") || file.isDirectory();
            }
        });

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(getClassesFromPackageByFile(path + "." + file.getName(), file));
            } else {
                String classname = file.getName().substring(0, file.getName().length() - 6);
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(path + "." + classname));
            }
        }

        return classes;
    }

    public static List<Class> getClassesFromPackageByJar(String path, File dir) throws ClassNotFoundException, IOException {
        List<Class> classes = new ArrayList<Class>();

        JarFile jar = new JarFile(dir);
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();

            if (!entry.isDirectory()
                    && entry.getName().startsWith(path.replace('.', '/'))
                    && entry.getName().endsWith(".class")) {

                classes.add(Thread.currentThread().getContextClassLoader().loadClass(entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6)));
            }
        }

        return classes;
    }
}
