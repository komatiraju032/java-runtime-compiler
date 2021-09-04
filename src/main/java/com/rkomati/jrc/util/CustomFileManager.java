package com.rkomati.jrc.util;

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import static javax.tools.StandardLocation.CLASS_PATH;
import static javax.tools.StandardLocation.PLATFORM_CLASS_PATH;

public class CustomFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final PackageFinder packageFinder;

    public CustomFileManager(ClassLoader classLoader, JavaFileManager fileManager) {
        super(fileManager);
        this.packageFinder = new PackageFinder(classLoader);
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof CustomFileObject)
            return ((CustomFileObject) file).binaryName();
        else
            return fileManager.inferBinaryName(location, file);
    }

    @Override
    public boolean hasLocation(Location location) {
        return location == StandardLocation.CLASS_PATH || location == StandardLocation.PLATFORM_CLASS_PATH;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        boolean baseModule = location.getName().equals("SYSTEM_MODULES[java.base]");
        if (baseModule || location == PLATFORM_CLASS_PATH) {
            return fileManager.list(location, packageName, kinds, recurse);
        } else if (location == CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
            if (packageName.startsWith("java") || packageName.startsWith("com.sun"))
                return fileManager.list(location, packageName, kinds, recurse);
            else
                return packageFinder.find(packageName);
        }
        return Collections.emptyList();
    }
}
