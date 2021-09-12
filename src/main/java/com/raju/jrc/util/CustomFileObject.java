package com.raju.jrc.util;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;

public class CustomFileObject implements JavaFileObject {

    private final String binaryName;
    private final URI uri;
    private final String name;

    public CustomFileObject(String binaryName, URI uri) {
        this.binaryName = binaryName;
        this.uri = uri;
        this.name = uri.getPath() == null ? uri.getSchemeSpecificPart() : uri.getPath();
    }

    @Override
    public Kind getKind() {
        return Kind.CLASS;
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        String fullName = simpleName + kind.extension;
        return kind.equals(getKind()) && (fullName.equals(getName()) || getName().endsWith("/" + fullName));
    }

    @Override
    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Modifier getAccessLevel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI toUri() {
        return uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return uri.toURL().openStream();
    }

    @Override
    public OutputStream openOutputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer openWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    public String binaryName() {
        return binaryName;

    }

    @Override
    public String toString() {
        return "CustomFileObject{" +
                "binaryName='" + binaryName + '\'' +
                ", uri=" + uri +
                ", name='" + name + '\'' +
                '}';
    }
}
