package annotation;

import annotation.model.PersonBuilder;

import annotation.model.Person;

public class App {

	public static void main(String[] args) {
		Person eliasMeireles = PersonBuilder.builder()
				.setName("Elias Meireles")
				.setAge(32)
				.build();

		System.out.println(eliasMeireles.getName());
	}
}
