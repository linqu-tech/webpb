import utils.Versions
import utils.signAndPublish

plugins {
    id("webpb.library")
}

dependencies {
    api(project(":libs:commons"))
    compileOnly("javax.servlet:javax.servlet-api:${Versions.servletApi}")
    compileOnly("org.springframework:spring-messaging:${Versions.springFramework}")
    compileOnly("org.springframework:spring-webflux:${Versions.springFramework}")
    compileOnly("org.springframework:spring-webmvc:${Versions.springFramework}")
    compileOnly(files(org.gradle.internal.jvm.Jvm.current().toolsJar))
    implementation("com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}")
    testImplementation("org.springframework:spring-webflux:${Versions.springFramework}")
}

signAndPublish("webpb-runtime") {
    from(components["java"])
    pom {
        description.set("The webpb runtime library for JAVA")
    }
}

tasks.javadoc {
    exclude("tech/linqu/webpb/runtime/mvc/WebpbRequestMappingProcessor.java")
    exclude("tech/linqu/webpb/runtime/utils/**")
}
