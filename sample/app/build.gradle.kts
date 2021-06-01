import com.google.protobuf.gradle.*
import utils.Vers

plugins {
    idea
    id("java.common")
    id("com.google.protobuf")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    annotationProcessor("tech.linqu.webpb:webpb-runtime:${Vers.webpb}")
    compileOnly("org.projectlombok:lombok:${Vers.lombok}")
    compileOnly("org.springframework:spring-web:${Vers.springFramework}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${Vers.jackson}")
    implementation("org.hibernate.validator:hibernate-validator:${Vers.hibernateValidator}")
    implementation("tech.linqu.webpb:webpb-proto:${Vers.webpb}")
    implementation("tech.linqu.webpb:webpb-runtime:${Vers.webpb}")
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
