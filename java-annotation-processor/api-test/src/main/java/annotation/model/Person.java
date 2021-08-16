package annotation.model;

import javax.inject.Singleton;

import com.softwareplace.annotation.ConfigSource;
import com.softwareplace.config.DataSource;

@Singleton
@ConfigSource
public class Person implements DataSource {

	private int age;

	private String name;

	//	public int getAge() {
	//		return age;
	//	}
	//
	//	@BuilderProperty
	//	public void setAge(int age) {
	//		this.age = age;
	//	}
	//
	//	public String getName() {
	//		return name;
	//	}
	//
	//	@BuilderProperty
	//	public void setName(String name) {
	//		this.name = name;
	//	}
}
