import utils.signAndPublish

plugins {
    `maven-publish`
    id("webpb.library")
    signing
}

tasks.jar {
    enabled = true
    from("src/main/proto") {
        include("**/*.proto")
    }
}

signAndPublish("webpb-proto") {
    artifact(tasks.jar.get())
    pom {
        description.set("Webpb common proto imports library")
    }
}
