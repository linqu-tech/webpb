import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    gradlePluginPortal()
}

val properties = Properties()
FileInputStream(file("../gradle.properties")).use {
    properties.load(it)
}

for (key in properties.stringPropertyNames()) {
    ext.set(key, properties.getProperty(key))
}

val pluginProtobufVersion: String by project
val pluginSpringBootVersion: String by project
val pluginVersionsVersion: String by project

dependencies {
    implementation("com.github.ben-manes:gradle-versions-plugin:${pluginVersionsVersion}")
    implementation("com.google.protobuf:protobuf-gradle-plugin:${pluginProtobufVersion}")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${pluginSpringBootVersion}")
    implementation(kotlin("script-runtime"))
}

tasks.create<Copy>("initGitHooks") {
    val hooksDir = project.file("src/main/resources/hooks")
    from(hooksDir)
    into(project.file("../.git/hooks"))
    fileMode = 0b111101101
}

tasks.build {
    dependsOn("initGitHooks")
}
