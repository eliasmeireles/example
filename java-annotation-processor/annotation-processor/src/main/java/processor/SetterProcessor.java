package processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("annotation.SetterProperty")
public class SetterProcessor extends AbstractProcessor {

	@Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		for (TypeElement annotation : annotations) {
			Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

			List<Element> setters = annotatedElements.stream()
					.filter(element -> element.getKind().isField())
					.collect(Collectors.toList());

			if (setters.isEmpty()) {
				continue;
			}

			String className = ((TypeElement) setters.get(0)
					.getEnclosingElement()).getQualifiedName().toString();

			try {
				Map<String, String> setterMap = setters.stream().collect(Collectors.toMap(
						setter -> setter.getSimpleName().toString(),
						setter -> setter.asType().toString()
				));

				writeBuilderFile(className, setterMap, setters.get(0));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return true;
	}


	private void writeBuilderFile(
			String className, Map<String, String> setterMap, Element element)
			throws IOException {

		className = replaceLast(className, ".", ".T");
		String packageName = null;
		int lastDot = className.lastIndexOf('.');
		if (lastDot > 0) {
			packageName = className.substring(0, lastDot);
		}


		JavaFileObject builderFile = processingEnv.getFiler()
				.createSourceFile(className, element);


		String simpleClassName = className.substring(lastDot + 1);

		try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
			out.print("package ");
			out.print(packageName);
			out.println(";");
			out.println();

			out.print("public class ");
			out.print(simpleClassName);
			out.println(" {");
			out.println();

			setterMap.forEach((fieldName, typeOf) -> {
				out.print("\tprivate ");
				out.print(typeOf);
				out.print(" ");
				out.print(fieldName);
				out.print(";");
				out.println();
			});

			setterMap.forEach((fieldName, typeOf) -> {
				out.print("\tpublic void set");
				out.print(capitalizeString(fieldName));
				out.print("(");
				out.print(typeOf);
				out.println(" value) {");
				out.print("\t\tthis.");
				out.print(fieldName);
				out.println(" = value;");
				out.println("\t}");
			});
			out.println("}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos)
					+ replacement
					+ string.substring(pos + toReplace.length());
		} else {
			return string;
		}
	}

	public static String capitalizeString(String str) {
		String retStr = str;
		try { // We can face index out of bound exception if the string is null
			retStr = str.substring(0, 1).toUpperCase() + str.substring(1);
		} catch (Exception e) {
		}
		return retStr;
	}
}
