package utils

import gradle.kotlin.dsl.accessors._9db69caf33bdadb52e152539254e937d.implementation
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.DocsType
import org.gradle.api.attributes.Usage
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.the
import org.gradle.plugins.signing.SigningExtension

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
            val release = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshot = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshot else release
            credentials(PasswordCredentials::class)
        }
    }
    configuration.execute(publication)
    project.the<SigningExtension>().sign(publication)
}

fun Project.createConfiguration(
    name: String,
    docsType: String,
    configuration: Action<Configuration>
): Configuration {
    val conf = configurations.create(name) {
        isVisible = false
        extendsFrom(configurations.implementation.get())
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(docsType))
        }
    }
    configuration.execute(conf)
    return conf
}
