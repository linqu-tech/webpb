package tech.linqu.webpb.sample.proto.store;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class StoreDataResponseTest {

    @Test
    void test() {
        StoreDataResponse response = new StoreDataResponse()
            .setStore(null);
        assertNotNull(response.webpbMeta());
        assertNull(response.getStore());
    }
}
