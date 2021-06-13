import utils.Vers
import utils.hierarchicalGroup

plugins {
    id("io.spring.dependency-management")
    id("java.common")
    id("org.springframework.boot")
    idea
}

group = hierarchicalGroup()

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    annotationProcessor(project(":runtime:processor"))
    compileOnly("org.projectlombok:lombok:${Vers.lombok}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${Vers.jackson}")
    implementation("org.hibernate.validator:hibernate-validator:${Vers.hibernateValidator}")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(project(":libs:proto"))
    implementation(project(":runtime:java"))
    implementation(project(":sample:proto"))
    testAnnotationProcessor("org.projectlombok:lombok:${Vers.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Vers.lombok}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Vers.jupiter}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework:spring-web:${Vers.springFramework}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    enabled = false
}
