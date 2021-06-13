import com.google.protobuf.gradle.protobuf

plugins {
  id("ts.frontend")
}

dependencies {
  protobuf(project(":sample:proto"))
}

task<Exec>("npmStart") {
  commandLine("npm", "run", "dev")

  dependsOn("generateProto")
}
