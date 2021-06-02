import com.google.protobuf.gradle.*
import utils.Vers
import utils.getGroupName

plugins {
    idea
    id("java.common")
    id("com.google.protobuf")
}

group = getGroupName()

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    compileOnly("org.projectlombok:lombok:${Vers.lombok}")
    implementation(project(":libs:proto"))
    implementation(project(":runtime:java"))
    testAnnotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Vers.lombok}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Vers.jupiter}")
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
        id("ts") {
            artifact = "tech.linqu.webpb:protoc-webpb-ts:${Vers.webpb}:all@jar"
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
                id("ts")
            }
        }
    }
}
