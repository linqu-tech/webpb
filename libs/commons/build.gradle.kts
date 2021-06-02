import utils.signAndPublish

plugins {
    id("java.library")
}

tasks.javadoc {
    enabled = false
}

signAndPublish("webpb-commons") {
    from(components["java"])
    pom {
        description.set("The webpb commons library")
    }
}
