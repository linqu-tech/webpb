package utils

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.DocsType
import org.gradle.api.attributes.Usage
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.the
import org.gradle.plugins.signing.SigningExtension
import java.io.File

fun Project.hierarchicalGroup(): String {
    var suffix = ""
    var proj = project.parent
    while (rootProject != proj && proj != null) {
        suffix = "." + proj.name + suffix
        proj = proj.parent!!
    }
    return project.group.toString() + suffix
}

fun Project.signAndPublish(artifactId: String, configuration: Action<MavenPublication>) {
    val extension = project.the<PublishingExtension>()
    val publicationName = "[_-]+[a-zA-Z]".toRegex().replace(artifactId) {
        it.value.replace("_", "").replace("-", "").capitalize()
    }
    val publication = extension.publications.create(publicationName, MavenPublication::class.java)
    publication.artifactId = artifactId
    publication.pom {
        name.set(publicationName)
        url.set("https://github.com/linqu-tech/webpb")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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
            url.set("https://github.com/linqu-tech/webpb")
        }
    }
    extension.repositories {
        maven {
            name = "oss"
            val release = uri(Props.releaseRepo)
            val snapshot = uri(Props.snapshotRepo)
            url = if (version.toString().endsWith("SNAPSHOT")) snapshot else release
            credentials {
                username = System.getenv("NEXUS_REPO_USERNAME")
                password = System.getenv("NEXUS_REPO_PASSWORD")
            }
        }
    }
    configuration.execute(publication)
    val signing = the<SigningExtension>()
    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassword = System.getenv("GPG_SIGNING_PASSWORD")
    signing.useInMemoryPgpKeys(signingKey, signingPassword)
    signing.sign(publication)
}

fun Project.createConfiguration(
    name: String,
    docsType: String,
    configuration: Action<Configuration>
): Configuration {
    val conf = configurations.create(name) {
        isVisible = false
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(docsType))
        }
    }
    configuration.execute(conf)
    return conf
}

fun Project.extractDependencies(file: File): List<String> {
    val text = file.readText()
    val versionRegex = "(.*)\\$\\{?([\\w+]*)}?".toRegex()
    return "(implementation|testImplementation)\\(\"(.*)\"\\)".toRegex()
        .findAll(text)
        .map { it.groupValues[2] }
        .map {
            val matchResult = versionRegex.find(it) ?: return@map it
            val artifact = matchResult.groupValues[1]
            val property = matchResult.groupValues[2]
            "$artifact${project.property(property) as String}"
        }
        .toList()
}
