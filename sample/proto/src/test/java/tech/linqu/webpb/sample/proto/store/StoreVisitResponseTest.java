package tech.linqu.webpb.sample.proto.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class StoreVisitResponseTest {

    @Test
    void test() {
        StoreVisitResponse response = new StoreVisitResponse()
            .setStore(null)
            .setGreeting("hello");
        assertNotNull(response.webpbMeta());
        assertNull(response.getStore());
        assertEquals("hello", response.getGreeting());
    }
}
