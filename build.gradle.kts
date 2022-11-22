import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.intellij") version "1.13.0"
    java
    kotlin("jvm") version "1.8.0"
}

group = "org.crystal.intellij"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java.srcDir("src/main/gen")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))
    implementation(kotlin("reflect"))
    testImplementation("junit", "junit", "4.12")
}

intellij {
    version.set(project.properties["ideaVersion"] as String)
    plugins.set(listOf("org.jetbrains.plugins.yaml"))
}

tasks {
    patchPluginXml {
        version.set(project.properties["version"] as String)
        sinceBuild.set(project.properties["ideaSinceBuild"] as String)
        untilBuild.set(project.properties["ideaUntilBuild"] as String)
    }

    publishPlugin {
        token.set(System.getenv("MARKETPLACE_TOKEN"))
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}