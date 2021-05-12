import com.google.protobuf.gradle.*

plugins {
    java
    idea
    id("com.google.protobuf") version ("0.8.16")
}

val lombokVersion: String by rootProject.extra
val webpbVersion = project.version

dependencies {
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    implementation("tech.linqu.webpb:webpb-proto:${webpbVersion}")
    implementation("tech.linqu.webpb:webpb-runtime:${webpbVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.15.8"
    }
    plugins {
        id("webpb") {
            artifact = "tech.linqu.webpb:protoc-webpb-java:${webpbVersion}:all@jar"
        }
        id("ts") {
            artifact = "tech.linqu.webpb:protoc-webpb-ts:${webpbVersion}:all@jar"
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
