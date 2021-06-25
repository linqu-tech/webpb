import io.spring.gradle.dependencymanagement.org.codehaus.plexus.util.Os

plugins {
  id("ts.runtime")
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
    commandLine(npm, "install", "--verbose")
  }
}

task<Exec>("npmStart") {
  commandLine(npm, "run", "dev")

  dependsOn("npmInstall")
}

task<Exec>("npmCheck") {
  commandLine(npm, "run", "lint")
  commandLine(npm, "run", "test")

  dependsOn("npmInstall")
}

tasks.check {
  dependsOn("npmCheck")
}
