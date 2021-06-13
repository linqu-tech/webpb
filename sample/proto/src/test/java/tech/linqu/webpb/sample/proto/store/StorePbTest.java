package tech.linqu.webpb.sample.proto.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class StorePbTest {

    @Test
    void test() {
        StorePb pb = new StorePb()
            .setId(11L)
            .setName("22")
            .setCity("33");
        assertNotNull(pb.webpbMeta());
        assertEquals(11L, pb.getId());
        assertEquals("22", pb.getName());
        assertEquals("33", pb.getCity());
    }
}
