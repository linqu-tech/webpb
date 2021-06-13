package tech.linqu.webpb.sample.proto.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class StoreListResponseTest {

    @Test
    void test() {
        StoreListResponse response = new StoreListResponse()
            .setPaging(null)
            .setStores(null)
            .setGreeting("11");
        assertNotNull(response.webpbMeta());
        assertNull(response.getPaging());
        assertNull(response.getStores());
        assertEquals("11", response.getGreeting());
    }
}
