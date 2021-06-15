package tech.linqu.webpb.sample.proto.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class StoreGreetingResponseTest {

    @Test
    void test() {
        StoreGreetingResponse response = new StoreGreetingResponse()
            .setGreeting("11");
        assertNotNull(response.webpbMeta());
        assertEquals("11", response.getGreeting());
    }
}
