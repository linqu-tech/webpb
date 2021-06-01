import utils.Vers

plugins {
    java
    id("conventions.versioning")
}

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
