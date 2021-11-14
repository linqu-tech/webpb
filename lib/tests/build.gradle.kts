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
    implementation(project(":lib:proto"))
}

file("src/proto/test").listFiles()?.filter { it.isDirectory }?.forEach {
    sourceSets.create(it.name) {
        proto {
            srcDir(it.absolutePath)
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Vers.protobuf}"
    }
    plugins {
        id("dump") {
            path = "${rootDir}/plugin/dump/build/libs/protoc-webpb-dump-${Vers.webpb}.jar"
        }
    }
    generateProtoTasks {
        sourceSets.forEach { set ->
            ofSourceSet(set.name).forEach {
                it.addIncludeDir(files("${buildDir}/extracted-include-protos/main"))
                it.builtins {
                    remove("java")
                }
                it.plugins {
                    id("dump")
                }
            }
        }
    }
}

tasks.jar {
    from("$buildDir/generated/source/proto")

    dependsOn(tasks
        .filter { "generate(\\w*)Proto".toRegex().matches(it.name) }
        .map { it.dependsOn("extractIncludeProto") }
    )
}

tasks.withType<GenerateProtoTask> {
    dependsOn(":plugin:dump:build")
}
