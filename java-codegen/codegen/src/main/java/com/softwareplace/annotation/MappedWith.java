package com.softwareplace.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.softwareplace.config.Config;

/**
 * {@link MappedWith#name()} is the  full path of the annotation
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface MappedWith {

	/**
	 * @return the name of the annotation used to inject {@link Config} implementation.
	 */
	String name();
}
