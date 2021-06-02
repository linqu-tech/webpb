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
    annotationProcessor(project(":runtime:java"))
    compileOnly("org.projectlombok:lombok:${Vers.lombok}")
    compileOnly("org.springframework:spring-web:${Vers.springFramework}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${Vers.jackson}")
    implementation("org.hibernate.validator:hibernate-validator:${Vers.hibernateValidator}")
    implementation(project(":libs:proto"))
    implementation(project(":runtime:java"))
    implementation(project(":sample:proto"))
    testAnnotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Vers.lombok}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Vers.jupiter}")
    testImplementation("org.springframework:spring-web:${Vers.springFramework}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

sourceSets {
    main {
        proto {
            srcDir("src/proto/app")
        }
    }
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
