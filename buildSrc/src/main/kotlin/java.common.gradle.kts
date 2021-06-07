import gradle.kotlin.dsl.accessors._b62b4054fca3097d153a801e2be568ce.annotationProcessor
import gradle.kotlin.dsl.accessors._b62b4054fca3097d153a801e2be568ce.compileOnly
import gradle.kotlin.dsl.accessors._b62b4054fca3097d153a801e2be568ce.testAnnotationProcessor
import gradle.kotlin.dsl.accessors._b62b4054fca3097d153a801e2be568ce.testCompileOnly
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

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule { limit { minimum = BigDecimal.valueOf(Props.jacocoMinCoverage) } }
        }
    }
    check {
        dependsOn(jacocoTestCoverageVerification)
    }
    test {
        finalizedBy(tasks.jacocoTestReport)
    }
    jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            csv.isEnabled = true
        }
    }
}
