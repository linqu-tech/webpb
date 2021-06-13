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
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Vers.protoc}"
    }
    plugins {
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
                id("ts")
            }
        }
    }
}
