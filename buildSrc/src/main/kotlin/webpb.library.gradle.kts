plugins {
    `java-library`
    `maven-publish`
    id("webpb.common")
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}
