import utils.Props
import utils.Vers
import utils.createConfiguration

plugins {
    checkstyle
    id("conventions.versioning")
    idea
    jacoco
    java
}

Props.initialize(project)
Vers.initialize(project)

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri(Props.snapshotRepo) }
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    compileOnly("org.projectlombok:lombok:${Vers.lombok}")
    testAnnotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Vers.lombok}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Vers.jupiter}")
    testImplementation("org.mockito:mockito-core:${Vers.mockito}")
    testImplementation("org.mockito:mockito-inline:${Vers.mockito}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

checkstyle {
    toolVersion = Vers.checkstyle
    configFile = file("${rootDir}/buildSrc/config/checkstyle.xml")
    isIgnoreFailures = false
    maxErrors = Props.checkstyleMaxErrors
    maxWarnings = Props.checkstyleMaxWarnings
}

jacoco {
    toolVersion = Vers.jacoco
}

tasks.jacocoTestReport {
    enabled = false
}

createConfiguration("outgoingClassDirs", "classDirs") {
    extendsFrom(configurations.implementation.get())
    isCanBeResolved = false
    isCanBeConsumed = true
    sourceSets.main.get().output.forEach {
        outgoing.artifact(it)
    }
}

createConfiguration("outgoingSourceDirs", "sourceDirs") {
    extendsFrom(configurations.implementation.get())
    isCanBeResolved = false
    isCanBeConsumed = true
    sourceSets.main.get().java.srcDirs.forEach {
        outgoing.artifact(it)
    }
}

createConfiguration("outgoingCoverageData", "coverageData") {
    extendsFrom(configurations.implementation.get())
    isCanBeResolved = false
    isCanBeConsumed = true
    outgoing.artifact(tasks.test.map {
        it.extensions.getByType<JacocoTaskExtension>().destinationFile!!
    })
}
