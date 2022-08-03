/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.5/userguide/building_java_projects.html
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    id("com.hivemq.extension")
}

group = "com.energymonitor.extension"
description = "Energy Monitor device auth extension"


hivemqExtension {
    name.set("Energy Monitor Extension")
    author.set("haytham boussarsar")
    priority.set(1000)
    startPriority.set(1000)
    mainClass.set("$group.App")
    sdkVersion.set("$version")
}



repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:31.0.1-jre")
}

application {
    // Define the main class for the application.
    mainClass.set("com.energymonitor.extension.App")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

tasks.runHivemqWithExtension {
    debugOptions {
        enabled.set(true)
    }
}
