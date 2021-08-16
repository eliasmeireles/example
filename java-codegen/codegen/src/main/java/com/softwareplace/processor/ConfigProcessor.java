package com.softwareplace.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.softwareplace.annotation.ConfigSource;
import com.softwareplace.annotation.MappedWith;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(value = {
		"com.softwareplace.annotation.ConfigSource",
		"com.softwareplace.annotation.MappedWith"
})
public class ConfigProcessor extends AbstractProcessor {

	private final Logger logger = Logger.getLogger(ConfigProcessor.class.getSimpleName());

	private static final String CONFIG_IMPL = "ConfigSourceImpl";
	private static final String DATA_SOURCE_INSTANCES = "{dataSourceInstances}";
	private static final String ANNOTATED_WITH = "{annotatedWith}";
	private static final String TEMPLATE = "ConfigImpl.template";

	@Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		List<Element> mapperAnnotations = new ArrayList<>(roundEnv.getElementsAnnotatedWith(MappedWith.class));
		List<Element> configSource = new ArrayList<>(roundEnv.getElementsAnnotatedWith(ConfigSource.class));

		if (!configSource.isEmpty()) {
			try {
				writeBuilderFile(configSource, mapperAnnotations.get(0));
			} catch (Exception error) {
				logger.log(Level.WARNING, error.getMessage());
			}
		}

		return true;
	}

	private void writeBuilderFile(List<Element> configSource, Element injectWithElement) throws IOException {
		String injectWith = injectWithElement.getAnnotation(MappedWith.class).name();
		String packageName = injectWithElement.getEnclosingElement().toString().concat(".config");
		String readeTemplate = templateBuilder(configSource, injectWith);

		JavaFileObject builderFile = processingEnv.getFiler()
				.createSourceFile(packageName.concat(".").concat(CONFIG_IMPL));

		try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
			out.println("package ".concat(packageName).concat(";"));
			out.println();
			out.print("import ");
			out.print(injectWith);
			out.println(";");
			addImport(configSource, out);
			out.print(readeTemplate);
		}
	}

	private String templateBuilder(List<Element> configSource, String injectWith) throws IOException {
		int lastDotAnnotation = injectWith.lastIndexOf('.');
		String annotation = injectWith.substring(lastDotAnnotation).replace(".", "");

		String addStatement = addClassStatement(configSource);
		return readeTemplateSourceFile(addStatement)
				.replace(ANNOTATED_WITH, "@".concat(annotation));
	}

	private String addClassStatement(List<Element> configSource) {
		return configSource.stream().map(data -> new StringBuilder()
						.append("objects.add(new ")
						.append(data.getSimpleName().toString())
						.append("());"))
				.collect(Collectors.joining("\n\t\t"));
	}

	private void addImport(List<Element> configSource, PrintWriter out) {
		configSource.stream().map(data -> new StringBuffer().append("import ")
				.append(data.getEnclosingElement().toString())
				.append(".")
				.append(data.getSimpleName())
				.append(";")).forEach(out::println);
	}

	private String readeTemplateSourceFile(String instancesAdd) throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(TEMPLATE);

		if (inputStream == null) {
			throw new IOException("Could not read file ".concat(TEMPLATE));
		}

		InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		BufferedReader reader = new BufferedReader(streamReader);

		String content = "";
		String line;
		while ((line = reader.readLine()) != null) {
			content = content.concat(line).concat("\n");
		}
		try {
			streamReader.close();
			reader.close();
		} catch (Exception error) {
			logger.log(Level.WARNING, error.getMessage());
		}

		return content.replace(DATA_SOURCE_INSTANCES, instancesAdd);
	}
}
