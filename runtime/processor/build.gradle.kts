import utils.Vers
import utils.signAndPublish

plugins {
    id("java.library")
}

dependencies {
    api(project(":runtime:java"))
    compileOnly(files(org.gradle.internal.jvm.Jvm.current().toolsJar))
    testImplementation("com.google.testing.compile:compile-testing:${Vers.compileTesting}")
    testImplementation("org.springframework:spring-messaging:${Vers.springFramework}")
    testImplementation("org.springframework:spring-web:${Vers.springFramework}")
    testImplementation(files(org.gradle.internal.jvm.Jvm.current().toolsJar))
}

signAndPublish("webpb-processor") {
    from(components["java"])
    pom {
        description.set("The webpb annotation processor for JAVA")
    }
}

tasks.javadoc {
    enabled = false
}
