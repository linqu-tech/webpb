import com.google.protobuf.gradle.*
import utils.Versions

plugins {
    java
    idea
    id("com.google.protobuf") version ("0.8.16")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    annotationProcessor("tech.linqu.webpb:webpb-runtime:${Versions.webpb}")
    compileOnly("org.projectlombok:lombok:${Versions.lombok}")
    compileOnly("org.springframework:spring-web:${Versions.springFramework}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${Versions.jackson}")
    implementation("org.hibernate.validator:hibernate-validator:${Versions.hibernateValidator}")
    implementation("tech.linqu.webpb:webpb-proto:${Versions.webpb}")
    implementation("tech.linqu.webpb:webpb-runtime:${Versions.webpb}")
    implementation(project(":sample:proto"))
    testAnnotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Versions.lombok}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.jupiter}")
    testImplementation("org.springframework:spring-web:${Versions.springFramework}")
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
        artifact = "com.google.protobuf:protoc:${Versions.protoc}"
    }
    plugins {
        id("webpb") {
            artifact = "tech.linqu.webpb:protoc-webpb-java:${Versions.webpb}:all@jar"
        }
        id("ts") {
            artifact = "tech.linqu.webpb:protoc-webpb-ts:${Versions.webpb}:all@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.addIncludeDir(files("src/proto/imports"))
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
