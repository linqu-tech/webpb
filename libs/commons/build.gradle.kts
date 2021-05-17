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
    `java-library`
    `maven-publish`
    signing
}

dependencies {
}

tasks.jar {
    enabled = true
}

val filename = "webpb-commons"

publishing {
    publications {
        create<MavenPublication>("webpbCommons") {
            artifactId = filename
            artifact(tasks.jar.get())
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}
val updatePublishing: (publishing: PublishingExtension, publication: String, filename: String, desc: String) -> Void by rootProject.extra
updatePublishing(publishing, "webpbCommons", filename, "The webpb commons library")

signing {
    sign(publishing.publications["webpbCommons"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
