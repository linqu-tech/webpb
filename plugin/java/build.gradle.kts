import utils.Vers
import utils.signAndPublish

plugins {
    id("java.application")
}

dependencies {
    implementation("com.github.javaparser:javaparser-core:${Vers.javaparser}")
    implementation(project(":lib:commons"))
    implementation(project(":lib:utilities"))
    testImplementation(project(":lib:tests"))
}

val artifactId = "protoc-webpb-${project.name}"

tasks.bootJar {
    archiveBaseName.set(artifactId)
    launchScript()
}

signAndPublish(artifactId) {
    artifact(tasks.bootJar.get()) { classifier = "all" }
    pom {
        description.set("The webpb protoc plugin for JAVA")
    }
}

