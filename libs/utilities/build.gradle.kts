import utils.Vers

plugins {
    id("java.library")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    compileOnly("org.projectlombok:lombok:${Vers.lombok}")
    implementation("com.google.protobuf:protobuf-java:${Vers.protobufJava}")
    implementation("org.apache.commons:commons-lang3:${Vers.commonsLang3}")
    implementation(project(":libs:commons"))
    testAnnotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Vers.lombok}")
}

tasks.javadoc {
    exclude("tech/linqu/webpb/utilities/descriptor/**")
}

tasks.withType<Checkstyle> {
    exclude("tech/linqu/webpb/utilities/descriptor/**")
}
