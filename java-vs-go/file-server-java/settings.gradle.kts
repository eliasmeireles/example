rootProject.name = "file-server"


buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://repo.spring.io/milestone")
    }

    dependencies {
        classpath("com.github.softwareplace.springboot:plugins:1.0.16")
    }
}

include(":app")


