import utils.Vers
import utils.signAndPublish

plugins {
    id("java.library")
}

dependencies {
    api(project(":lib:commons"))
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Vers.jackson}")
    compileOnly("javax.servlet:javax.servlet-api:${Vers.servletApi}")
    compileOnly("org.springframework:spring-messaging:${Vers.springFramework}")
    compileOnly("org.springframework:spring-webflux:${Vers.springFramework}")
    compileOnly("org.springframework:spring-webmvc:${Vers.springFramework}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${Vers.jackson}")
    testImplementation("io.projectreactor.netty:reactor-netty:${Vers.reactorNetty}")
    testImplementation("javax.servlet:javax.servlet-api:${Vers.servletApi}")
    testImplementation("org.springframework:spring-test:${Vers.springFramework}")
    testImplementation("org.springframework:spring-webflux:${Vers.springFramework}")
    testImplementation("org.springframework:spring-webmvc:${Vers.springFramework}")
}

signAndPublish("webpb-runtime") {
    from(components["java"])
    pom {
        description.set("The webpb runtime library for JAVA")
    }
}
