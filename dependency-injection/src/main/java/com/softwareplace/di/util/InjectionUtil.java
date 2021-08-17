package com.softwareplace.di.util;

import java.lang.reflect.Field;
import java.util.Collection;

import org.burningwave.core.classes.FieldCriteria;
import org.burningwave.core.classes.Fields;

import com.softwareplace.di.annotation.Autowired;
import com.softwareplace.di.annotation.Qualifier;
import com.softwareplace.di.inject.Injector;

public class InjectionUtil {
	private InjectionUtil() {
	}

	public static void autowire(Injector injector, Class<?> classy, Object classInstance)
			throws IllegalAccessException, InstantiationException {
		final Collection<Field> fields = Fields.create().findAll(getFieldCriteria(), classy);
		for (Field field : fields) {
			autowireField(injector, field, classInstance);
		}
	}

	private static void autowireField(Injector injector, Field field, Object classInstance)
			throws IllegalAccessException, InstantiationException {
		final String qualifier = field.isAnnotationPresent(Qualifier.class) ? field.getAnnotation(Qualifier.class).value() : null;
		Object fieldInstance = injector.getBeanInstance(field.getType(), field.getName(), qualifier);
		Fields.create().setDirect(classInstance, field, fieldInstance);
		autowire(injector, fieldInstance.getClass(), fieldInstance);
	}

	private static FieldCriteria getFieldCriteria() {
		return FieldCriteria.forEntireClassHierarchy().allThoseThatMatch(InjectionUtil::hasAutowiredAnnotation);
	}

	private static boolean hasAutowiredAnnotation(Field field) {
		return field.isAnnotationPresent(Autowired.class);
	}
}
