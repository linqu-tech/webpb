import com.google.protobuf.gradle.*
import utils.Props
import utils.Vers

plugins {
    id("com.google.protobuf")
    id("conventions.versioning")
    java
}

Props.initialize(project)
Vers.initialize(project)

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri(Props.snapshotRepo) }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Vers.protobuf}"
    }
    plugins {
        id("ts") {
            path = "${rootDir}/plugin/ts/build/libs/protoc-webpb-ts-${Vers.webpb}.jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.builtins {
                remove("java")
            }
            it.plugins {
                id("ts")
            }
        }
    }
}

tasks.withType<GenerateProtoTask> {
    dependsOn(":plugin:ts:build")
}
