import utils.Props
import utils.Vers

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
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Vers.jupiter}")
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

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule { limit { minimum = BigDecimal.valueOf(Props.jacocoMinCoverage) } }
        }
    }
    check {
        dependsOn(jacocoTestCoverageVerification)
    }
}
