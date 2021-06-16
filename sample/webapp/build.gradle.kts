import com.google.protobuf.gradle.protobuf

plugins {
  id("ts.frontend")
}

dependencies {
  protobuf(project(":sample:proto"))
}

task<Exec>("npmInstall") {
  val nodeModules = file("./node_modules")
  if (nodeModules.exists()) {
    commandLine("npm", "--version")
  } else{
    commandLine("npm", "install")
  }
}

task<Exec>("npmStart") {
  commandLine("npm", "run", "dev")

  dependsOn("npmInstall")
  dependsOn("generateProto")
}

task<Exec>("npmCheck") {
  commandLine("npm", "run", "lint")
  commandLine("npm", "run", "test")

  dependsOn("npmInstall")
  dependsOn("generateProto")
}

tasks.check {
  dependsOn("npmCheck")
}
