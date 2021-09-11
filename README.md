# java-runtime-compiler
helps in compiling the java file at runtime

For compiling simple java code use below code
```java
Class<?> cls = RuntimeCompiler.compile(sourceCodeString, "fullyqualifiedClassName");

// we can also specify file path
Class<?> cls = RuntimeCompiler.compile(Paths.get("filePath"), "fullyqualifiedClassName");
```

For compiling class that uses spring annotations or spring beans
```java
Object obj = beanCompiler.compileAndGetSpringBean(sourceCodeString, "fullyqualifiedClassName");

//using file path
Object obj = beanCompiler.compileAndGetSpringBean(Paths.get("filePath"), "fullyqualifiedClassName");
```
