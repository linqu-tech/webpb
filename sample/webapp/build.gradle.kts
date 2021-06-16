import com.google.protobuf.gradle.protobuf
import io.spring.gradle.dependencymanagement.org.codehaus.plexus.util.Os

plugins {
  id("ts.frontend")
}

dependencies {
  protobuf(project(":sample:proto"))
}

var npm = "npm";
if (Os.isFamily(Os.FAMILY_WINDOWS)) {
  npm = "npm.cmd"
}

task<Exec>("npmInstall") {
  val nodeModules = file("./node_modules")
  if (nodeModules.exists()) {
    commandLine(npm, "--version")
  } else {
    commandLine(npm, "install")
  }
}

task<Exec>("npmStart") {
  commandLine(npm, "run", "dev")

  dependsOn("npmInstall")
  dependsOn("generateProto")
}

task<Exec>("npmCheck") {
  commandLine(npm, "run", "lint")
  commandLine(npm, "run", "test")

  dependsOn("npmInstall")
  dependsOn("generateProto")
}

tasks.check {
  dependsOn("npmCheck")
}
