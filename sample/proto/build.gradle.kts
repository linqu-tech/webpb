import com.google.protobuf.gradle.*
import utils.Vers
import utils.hierarchicalGroup

plugins {
    idea
    id("java.common")
    id("com.google.protobuf")
}

group = hierarchicalGroup()

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    compileOnly("org.projectlombok:lombok:${Vers.lombok}")
    compileOnly(project(":runtime:java"))
    implementation(project(":libs:proto"))
    testAnnotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Vers.lombok}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Vers.jupiter}")
    testImplementation(project(":runtime:java"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Vers.protoc}"
    }
    plugins {
        id("webpb") {
            artifact = "tech.linqu.webpb:protoc-webpb-java:${Vers.webpb}:all@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.builtins {
                remove("java")
            }
            it.plugins {
                id("webpb") {
                    outputSubDir = "java"
                }
            }
        }
    }
}
