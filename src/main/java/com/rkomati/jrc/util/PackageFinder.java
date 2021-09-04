package com.rkomati.jrc.util;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;

import static javax.tools.JavaFileObject.Kind.CLASS;

public class PackageFinder {
    private final ClassLoader classLoader;

    public PackageFinder(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Iterable<JavaFileObject> find(String packageName) throws IOException {
        String javaPackageName = packageName.replaceAll("\\.", "/");
        List<JavaFileObject> javaFileObjects = new ArrayList<>();

        Enumeration<URL> urlEnumeration = classLoader.getResources(javaPackageName);
        while (urlEnumeration.hasMoreElements()) {
            URL packageURL = urlEnumeration.nextElement();
            javaFileObjects.addAll(getFileObjectsFromPackageURL(packageName, packageURL));
        }
        return javaFileObjects;
    }

    private Collection<? extends JavaFileObject> getFileObjectsFromPackageURL(String packageName, URL packageURL) {
        File directory = new File(packageURL.getFile());
        return directory.isDirectory() ? processDirectory(packageName, directory) : processJar(packageURL);
    }

    private Collection<? extends JavaFileObject> processDirectory(String packageName, File directory) {
        List<JavaFileObject> javaFileObjects = new ArrayList<>();
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile() && file.getName().endsWith(CLASS.extension)) {
                String binaryName = packageName + "." + file.getName();
                binaryName = binaryName.replaceAll(CLASS.extension + "$", "");
                javaFileObjects.add(new CustomFileObject(binaryName, file.toURI()));
            }
        }
        return javaFileObjects;
    }


    private List<JavaFileObject> processJar(URL packageURL) {
        List<JavaFileObject> javaFileObjects = new ArrayList<>();
        try {
            String externalForm = packageURL.toExternalForm();
            String jarURI = externalForm.substring(0, externalForm.lastIndexOf('!'));

            JarURLConnection connection = (JarURLConnection) packageURL.openConnection();
            String rootEntryName = connection.getEntryName();
            int rootEnd = rootEntryName.length() + 1;

            Enumeration<JarEntry> jarEntryEnumeration = connection.getJarFile().entries();
            while (jarEntryEnumeration.hasMoreElements()) {
                JarEntry entry = jarEntryEnumeration.nextElement();
                String name = entry.getName();
                if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1 && name.endsWith(CLASS.extension)) {
                    URI uri = URI.create(jarURI + "!/" + name);
                    String binaryName = name.replaceAll("/", ".");
                    binaryName = binaryName.replaceAll(CLASS.extension + "$", "");
                    javaFileObjects.add(new CustomFileObject(binaryName, uri));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to open %s as a jar file", packageURL), e);
        }
        return javaFileObjects;
    }


}
