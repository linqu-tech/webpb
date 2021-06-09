import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import utils.Vers

plugins {
    id("com.google.protobuf")
    id("java.library")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    compileOnly("org.projectlombok:lombok:${Vers.lombok}")
    implementation("com.google.protobuf:protobuf-java:${Vers.protobufJava}")
    implementation("org.apache.commons:commons-lang3:${Vers.commonsLang3}")
    implementation(project(":libs:commons"))
    implementation(project(":libs:proto"))
    testAnnotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Vers.lombok}")
}

val extractSources by tasks.registering(DefaultTask::class) {
    sourceSets {
        main {
            proto {
                srcDir("$buildDir/extracted-include-protos/main/webpb")
            }
        }
    }
    dependsOn("extractIncludeProto")
}

tasks.processResources {
    dependsOn(extractSources)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Vers.protoc}"
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.dependsOn(extractSources)
        }
    }
}
