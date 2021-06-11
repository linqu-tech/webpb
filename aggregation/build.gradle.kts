import utils.createConfiguration

plugins {
    id("jacoco.aggregation")
}

dependencies {
    implementation(project(":libs:commons"))
    implementation(project(":libs:proto"))
    implementation(project(":libs:utilities"))
    implementation(project(":plugins:dump"))
    implementation(project(":plugins:java"))
    implementation(project(":plugins:ts"))
    implementation(project(":runtime:java"))
    implementation(project(":runtime:processor"))
    implementation(project(":sample:app"))
    implementation(project(":sample:proto"))
}

configurations.implementation.get().dependencies.forEach {
    if (it is ModuleDependency) {
        it.isTransitive = false
    }
}

val incomingClassDirs = createConfiguration("incomingClassDirs", "classDirs") {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val incomingSourceDirs = createConfiguration("incomingSourceDirs", "sourceDirs") {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val incomingCoverageData = createConfiguration("incomingCoverageData", "coverageData") {
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
        xml.isEnabled = true
        html.isEnabled = true
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
