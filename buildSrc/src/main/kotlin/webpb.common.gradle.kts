import utils.Const
import utils.Versions

plugins {
    java
    id("webpb.versioning")
}

group = Const.group
version = Const.version

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
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.jupiter}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}
