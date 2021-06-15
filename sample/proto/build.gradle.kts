import utils.hierarchicalGroup

plugins {
    id("java.common")
}

group = hierarchicalGroup()

dependencies {
    implementation(project(":libs:proto"))
}
