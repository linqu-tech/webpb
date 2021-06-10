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
    implementation("commons-io:commons-io:2.9.0")
    implementation(project(":libs:proto"))
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
        artifact = "com.google.protobuf:protoc:${Vers.protoc}"
    }
    plugins {
        id("dump") {
            artifact = "tech.linqu.webpb:protoc-webpb-dump:${Vers.webpb}:all@jar"
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
