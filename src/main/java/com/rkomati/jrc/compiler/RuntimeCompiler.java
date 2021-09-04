package com.rkomati.jrc.compiler;

import com.rkomati.jrc.util.CustomFileManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class RuntimeCompiler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeCompiler.class);


    public static Class<?> compile(String content, String className) throws IOException, ClassNotFoundException {
        Path filePath = Paths.get(FileUtils.getTempDirectoryPath(), className.substring(className.lastIndexOf('.') + 1) + ".java");
        Files.deleteIfExists(filePath);
        Path tempFile = Files.createFile(filePath);
        Files.write(tempFile, content.getBytes(StandardCharsets.UTF_8));
        Class<?> cls = compile(tempFile, className);
        Files.delete(tempFile);
        return cls;
    }

    public static Class<?> compile(Path filePath, String className) throws IOException, ClassNotFoundException {
        File file = filePath.toFile();
        Path tempFolderPath = FileUtils.getTempDirectory().toPath();
        Path targetFolderPath = Files.createTempDirectory(tempFolderPath, "target");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> listener = new DiagnosticCollector<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(listener, null, null);
        JavaFileManager fileManager = new CustomFileManager(classLoader, standardJavaFileManager);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, listener, Arrays.asList("-d", targetFolderPath.toString()), null, standardJavaFileManager.getJavaFileObjects(file));

        if (!task.call()) {
            listener.getDiagnostics().forEach(d -> LOGGER.info(d.getMessage(null)));
        } else {
            LOGGER.info("Compilation completed successfully!");
        }

        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{targetFolderPath.toUri().toURL()}, classLoader);
        Class<?> cls = Class.forName(className, true, urlClassLoader);
        FileUtils.deleteDirectory(targetFolderPath.toFile());
        return cls;
    }
}


