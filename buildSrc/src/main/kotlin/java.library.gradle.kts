plugins {
    `java-library`
    `maven-publish`
    id("java.common")
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}
