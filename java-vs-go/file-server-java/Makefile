ARCH := $(shell uname -m)
COMPILED_DIR := ./compiled/$(ARCH)
APP_BINARY := ./app/build/native/nativeCompile/app

rebuild:
	./gradlew clean build


jar-content:
	jar tf app/build/libs/app-1.0.0.jar

run:
	./gradlew clean bootRun

test:
	./gradlew clean test

clean:
	./gradlew clean


local-deployment:
	@docker-compose -p filer-server-java up --build -d


