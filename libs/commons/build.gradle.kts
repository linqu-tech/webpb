import utils.signAndPublish

plugins {
    id("java.library")
}

signAndPublish("webpb-commons") {
    from(components["java"])
    pom {
        description.set("The webpb commons library")
    }
}

tasks.javadoc {
    enabled = false
}
