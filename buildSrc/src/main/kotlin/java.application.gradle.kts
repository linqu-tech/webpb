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
    annotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    compileOnly("org.projectlombok:lombok:${Vers.lombok}")
    implementation("com.google.protobuf:protobuf-java:${Vers.protobufJava}")
    implementation("org.apache.commons:commons-lang3:${Vers.commonsLang3}")
    testAnnotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Vers.lombok}")
}
