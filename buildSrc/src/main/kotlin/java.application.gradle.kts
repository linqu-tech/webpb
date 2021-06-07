import utils.Vers

plugins {
    java
    `maven-publish`
    application
    id("java.common")
    id("org.springframework.boot")
    signing
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:${Vers.protobufJava}")
    implementation("org.apache.commons:commons-lang3:${Vers.commonsLang3}")
}
