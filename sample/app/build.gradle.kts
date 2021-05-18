/*
 * Copyright (c) 2020 linqu.tech, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.google.protobuf.gradle.*

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

val lombokVersion: String by rootProject.extra
val webpbVersion = project.version

dependencies {
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("tech.linqu.webpb:webpb-runtime:${webpbVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("org.springframework:spring-web:5.3.6")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.3")
    implementation("org.hibernate.validator:hibernate-validator:7.0.1.Final")
    implementation("tech.linqu.webpb:webpb-proto:${webpbVersion}")
    implementation("tech.linqu.webpb:webpb-runtime:${webpbVersion}")
    implementation(project(":sample:proto"))
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testImplementation("org.springframework:spring-web:5.3.6")
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
