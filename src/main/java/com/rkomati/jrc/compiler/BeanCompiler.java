package com.rkomati.jrc.compiler;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class BeanCompiler {

    private final AutowireCapableBeanFactory factory;

    public BeanCompiler(AutowireCapableBeanFactory factory) {
        this.factory = factory;
    }

    public Object compileAndGetSpringBean(String content, String className) throws IOException, ClassNotFoundException {
        Class<?> cls = RuntimeCompiler.compile(content, className);
        return factory.createBean(cls, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }

    public Object compileAndGetSpringBean(Path filePath, String className) throws IOException, ClassNotFoundException {
        Class<?> cls = RuntimeCompiler.compile(filePath, className);
        return factory.createBean(cls, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }
}
