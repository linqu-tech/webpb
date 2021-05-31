plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("script-runtime"))
    implementation("com.github.ben-manes:gradle-versions-plugin:0.38.0")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.5.0")
}
