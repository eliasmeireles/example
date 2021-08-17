package com.softwareplace.di.inject;

import static org.burningwave.core.classes.ClassHunter.SearchResult;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.core.classes.CacheableSearchConfig;
import org.burningwave.core.classes.ClassCriteria;
import org.burningwave.core.classes.ClassHunter;
import org.burningwave.core.classes.SearchConfig;

import com.softwareplace.di.annotation.Component;
import com.softwareplace.di.annotation.Qualifier;
import com.softwareplace.di.util.InjectionUtil;

public class Injector {
	private static Injector injector;
	private final Map<Class<?>, Class<?>> diMap;
	private final Map<Class<?>, Object> applicationScope;

	private Injector() {
		diMap = new HashMap<>();
		applicationScope = new HashMap<>();
	}

	public static void startApplication(Class<?> mainClass) {
		try {
			synchronized (Injector.class) {
				if (injector == null) {
					injector = new Injector();
					injector.initFramework(mainClass);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static <T> T getService(Class<T> classy) {
		try {
			return injector.getBeanInstance(classy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void initFramework(Class<?> mainClass) throws InstantiationException, IllegalAccessException {
		String packageRealPath = mainClass.getPackage().getName().replace(".", File.separator);

		final CacheableSearchConfig searchConfig = SearchConfig.forResources(packageRealPath);
		final ClassCriteria classCriteria = ClassCriteria.create();

		try (SearchResult searchResult = getClassHunter()
				.findBy(searchConfig.by(classCriteria.allThoseThatMatch(this::hasComponentAnnotation)))) {

			final Collection<Class<?>> types = searchResult.getClasses();
			types.forEach(this::getImplementation);

			final Class<?>[] classes = getClasses(packageRealPath);
			extractClassesWithComponentAnnotation(classes);
		}
	}

	private void extractClassesWithComponentAnnotation(Class<?>[] classes) throws InstantiationException, IllegalAccessException {
		for (Class<?> classy : classes) {
			if (classy.isAnnotationPresent(Component.class)) {
				final Object classInstance = classy.newInstance();
				applicationScope.put(classy, classInstance);
				InjectionUtil.autowire(this, classy, classInstance);
			}
		}
	}

	private void getImplementation(Class<?> implementationClass) {
		final Class<?>[] interfaces = implementationClass.getInterfaces();
		if (interfaces.length == 0) {
			diMap.put(implementationClass, implementationClass);
		} else {
			for (Class<?> classInterface : interfaces) {
				diMap.put(implementationClass, classInterface);
			}
		}
	}

	private boolean hasComponentAnnotation(Class<?> cls) {
		return cls.getAnnotation(Component.class) != null;
	}

	private Class<?>[] getClasses(String packageRealPath) {
		ClassHunter classHunter = getClassHunter();
		CacheableSearchConfig config = SearchConfig.forResources(packageRealPath);
		config.notRecursiveOnPath(packageRealPath, true);

		try (SearchResult result = classHunter.findBy(config)) {
			Collection<Class<?>> classes = result.getClasses();
			return classes.toArray(new Class[0]);
		}
	}

	private ClassHunter getClassHunter() {
		ComponentContainer componentContainer = ComponentContainer.getInstance();
		return componentContainer.getClassHunter();
	}

	@SuppressWarnings("unchecked")
	private <T> T getBeanInstance(Class<T> interfaceClass) throws InstantiationException, IllegalAccessException {
		return (T) getBeanInstance(interfaceClass, null, null);
	}

	public <T> Object getBeanInstance(Class<T> interfaceClass, String fieldName, String qualifier) throws InstantiationException, IllegalAccessException {
		Class<?> implementationClass = getImplementationClass(interfaceClass, fieldName, qualifier);

		if (applicationScope.containsKey(implementationClass)) {
			return applicationScope.get(implementationClass);
		}
		synchronized (applicationScope) {
			final Object service = implementationClass.newInstance();
			applicationScope.put(implementationClass, service);
			return service;
		}
	}

	private <T> Class<?> getImplementationClass(Class<T> interfaceClass, String fieldName, String qualifier) {
		final Set<Map.Entry<Class<?>, Class<?>>> implementationClasses = diMap.entrySet().stream()
				.filter(entry -> entry.getValue() == interfaceClass)
				.collect(Collectors.toSet());

		if (implementationClasses.size() == 1) {
			final Optional<Map.Entry<Class<?>, Class<?>>> optional = implementationClasses.stream().findFirst();

			//noinspection ConstantConditions
			if (optional.isPresent()) {
				return optional.get().getKey();
			}
		} else if (!implementationClasses.isEmpty()) {
			return getImplementationClass(interfaceClass, fieldName, qualifier, implementationClasses);
		}

		throw new RuntimeErrorException(new Error("No implementation found for interface " + interfaceClass.getName()));
	}

	private Class<?> getImplementationClass(Class<?> interfaceClass, String fieldName, String qualifier,
			Set<Map.Entry<Class<?>, Class<?>>> implementationClasses) {
		String errorMessage;
		final String findBy = (qualifier == null || qualifier.trim().isEmpty()) ? fieldName : qualifier;
		final Optional<Map.Entry<Class<?>, Class<?>>> optional = implementationClasses.stream()
				.filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(findBy))
				.findAny();

		if (optional.isPresent()) {
			return optional.get().getKey();
		}

		final String qualifierFullName = Qualifier.class.getName();
		errorMessage = "There are " + implementationClasses.size() + " of interface " + interfaceClass.getName()
				+ " Expected single implementation or make use of " + qualifierFullName + " to resolve conflict";

		throw new RuntimeErrorException(new Error(errorMessage));
	}
}
