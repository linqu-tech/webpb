import utils.createConfiguration
import utils.extractDependencies

plugins {
    id("jacoco.aggregation")
}

repositories {
    gradlePluginPortal()
}

val buildSrcDependencies = extractDependencies(file("${rootDir}/buildSrc/build.gradle.kts"))

dependencies {
    implementation(project(":libs:commons"))
    implementation(project(":libs:proto"))
    implementation(project(":libs:utilities"))
    implementation(project(":plugins:dump"))
    implementation(project(":plugins:java"))
    implementation(project(":plugins:ts"))
    implementation(project(":runtime:java"))
    implementation(project(":runtime:processor"))
    implementation(project(":sample:proto"))
    implementation(project(":sample:spring"))

    buildSrcDependencies.forEach {
        testCompileOnly(it)
    }
}

configurations.implementation.get().dependencies.forEach {
    if (it is ModuleDependency) {
        it.isTransitive = false
    }
}

val incomingClassDirs = createConfiguration("incomingClassDirs", "classDirs") {
    extendsFrom(configurations.implementation.get())
    isCanBeResolved = true
    isCanBeConsumed = false
}

val incomingSourceDirs = createConfiguration("incomingSourceDirs", "sourceDirs") {
    extendsFrom(configurations.implementation.get())
    isCanBeResolved = true
    isCanBeConsumed = false
}

val incomingCoverageData = createConfiguration("incomingCoverageData", "coverageData") {
    extendsFrom(configurations.implementation.get())
    isCanBeResolved = true
    isCanBeConsumed = false
}

fun updateJacocoReport(base: JacocoReportBase) {
    base.additionalClassDirs(incomingClassDirs.incoming.artifactView {
        lenient(true)
    }.files.asFileTree.matching {
        exclude("tech/linqu/webpb/processor/misc/*")
        exclude("tech/linqu/webpb/utilities/descriptor/*")
    })
    base.additionalSourceDirs(incomingSourceDirs.incoming.artifactView { lenient(true) }.files)
    base.executionData(incomingCoverageData.incoming.artifactView { lenient(true) }.files.filter { it.exists() })
}

val coverageVerification by tasks.registering(JacocoCoverageVerification::class) {
    updateJacocoReport(this)

    violationRules {
        rule { limit { minimum = BigDecimal.valueOf(utils.Props.jacocoMinCoverage) } }
    }
}

val coverage by tasks.registering(JacocoReport::class) {
    updateJacocoReport(this)

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

val configCoveralls by tasks.registering(DefaultTask::class) {
    coveralls {
        sourceDirs = incomingSourceDirs.incoming.artifactView { lenient(true) }.files.map {
            it.absolutePath
        }
        jacocoReportPath = "build/reports/jacoco/coverage/coverage.xml"
    }
}

tasks.coveralls {
    dependsOn(configCoveralls)
}

tasks.check {
    dependsOn(coverageVerification)
}
