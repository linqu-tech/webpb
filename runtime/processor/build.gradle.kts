import utils.signAndPublish

plugins {
    id("java.library")
}

dependencies {
    api(project(":runtime:java"))
    compileOnly(files(org.gradle.internal.jvm.Jvm.current().toolsJar))
}

tasks.javadoc {
    enabled = false
}

signAndPublish("webpb-processor") {
    from(components["java"])
    pom {
        description.set("The webpb annotation processor for JAVA")
    }
}
