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
plugins {
    java
    id("com.github.ben-manes.versions") version ("0.38.0")
    id("org.springframework.boot") version ("2.4.5")
}

val lombokVersion by extra { "1.18.20" }
val protobufVersion by extra { "3.15.8" }
val commonsLangVersion by extra { "3.12.0" }

tasks.bootJar {
    enabled = false
}

allprojects {
    apply(plugin = "java")

    group = "tech.linqu.webpb"
    version = "0.0.1-SNAPSHOT"

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

configure(subprojects.filter { it.parent?.name == "plugins" }) {
    apply(plugin = "org.springframework.boot")

    dependencies {
        annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
        compileOnly("org.projectlombok:lombok:${lombokVersion}")
        implementation("com.google.protobuf:protobuf-java:${protobufVersion}")
        implementation("org.apache.commons:commons-lang3:${commonsLangVersion}")
        testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
        testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(arrayOf(
        "-Xlint:deprecation",
        "-Xlint:unchecked",
        "-Amapstruct.unmappedTargetPolicy=IGNORE"
    ))
}
