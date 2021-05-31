import utils.Versions

plugins {
    `maven-publish`
    application
    id("org.springframework.boot")
    id("webpb.common")
    signing
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    compileOnly("org.projectlombok:lombok:${Versions.lombok}")
    implementation("com.google.protobuf:protobuf-java:${Versions.protobufJava}")
    implementation("org.apache.commons:commons-lang3:${Versions.commonsLang3}")
    testAnnotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Versions.lombok}")
}
