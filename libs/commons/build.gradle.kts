import utils.signAndPublish

plugins {
    id("webpb.library")
}

signAndPublish("webpb-commons") {
    from(components["java"])
    pom {
        description.set("The webpb commons library")
    }
}
