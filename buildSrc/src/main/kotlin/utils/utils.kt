package utils

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.credentials
import org.gradle.plugins.signing.SigningExtension

fun Project.getGroupName(): String {
    var suffix = ""
    var proj = project.parent
    while (rootProject != proj && proj != null) {
        suffix = "." + proj.name + suffix
        proj = proj.parent!!
    }
    return project.group.toString() + suffix
}

fun Project.signAndPublish(artifactId: String, configuration: Action<MavenPublication>) {
    val extension = project.extensions.getByType(PublishingExtension::class.java)
    val publicationName = "([a-zA-Z]*)".toRegex().replace(artifactId) { it.value.capitalize() }
    val publication = extension.publications.create(publicationName, MavenPublication::class.java, configuration)
    publication.artifactId = artifactId
    publication.pom {
        name.set(publicationName)
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
                name.set("JinGan")
                email.set("156023966@qq.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/linqu-tech/webpb.git")
            developerConnection.set("scm:git:ssh://github.com/linqu-tech/webpb.git")
            url.set("http://github.com/linqu-tech/webpb")
        }
    }
    extension.repositories {
        maven {
            name = "oss"
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials(PasswordCredentials::class)
        }
    }
    project.extensions.getByType(SigningExtension::class.java).sign(publication)
}
