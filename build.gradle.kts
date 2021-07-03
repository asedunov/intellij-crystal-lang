plugins {
    id("org.jetbrains.intellij") version "1.0"
    java
    kotlin("jvm") version "1.5.0"
}

group = "org.crystal.intellij"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java.srcDir("src/main/gen")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("junit", "junit", "4.12")
}

intellij {
    version.set("2021.1.3")
}