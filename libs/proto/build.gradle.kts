import utils.signAndPublish

plugins {
    `maven-publish`
    id("java.library")
    signing
}

signAndPublish("webpb-proto") {
    artifact(tasks.jar.get())
    pom {
        description.set("Webpb common proto imports library")
    }
}
