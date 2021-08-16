# Code Generator

- Simple test code generator application

Enable build runner

```shell
chmod +x build_generate
```

Generate new jar

- By running the command bellow, a new `codegen-0.0.1.jar` will be created and replaced the last one at `~/.m2/repository/com/softwareplace/codegen/0.0.1/`

```shell
./build_generate
```

Add maven dependency

```xml

<dependency>
    <groupId>com.softwareplace</groupId>
    <artifactId>codegen</artifactId>
    <version>0.0.1</version>
</dependency>
```
