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
    `maven-publish`
    signing
}

dependencies {
    implementation(project(":libs:commons"))
    implementation(project(":libs:utilities"))
}

val filename = "protoc-webpb-${project.name}"

tasks.bootJar {
    archiveBaseName.set(filename)
    launchScript()
}

publishing {
    publications {
        create<MavenPublication>("webpbTs") {
            artifactId = filename
            artifact(tasks.bootJar.get()) { classifier = "all" }
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set(filename)
                description.set("The webpb protoc plugin for ts")
                url.set("https://github.com/linqu-tech/webpb")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("gan.jin")
                        name.set("Gan Jin")
                        email.set("156023966@qq.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/linqu-tech/webpb.git")
                    developerConnection.set("scm:git:ssh://github.com/linqu-tech/webpb.git")
                    url.set("http://github.com/linqu-tech/webpb")
                }
            }
        }
    }
    repositories {
        maven {
            name = "oss"
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    sign(publishing.publications["webpbTs"])
}
