package tech.linqu.webpb.sample.proto.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class PagingPbTest {

    @Test
    void test() {
        PagingPb pb = new PagingPb()
            .setPage(11)
            .setSize(22)
            .setTotalCount(33)
            .setTotalPage(44);
        assertNotNull(pb.webpbMeta());
        assertEquals(11, pb.getPage());
        assertEquals(22, pb.getSize());
        assertEquals(33, pb.getTotalCount());
        assertEquals(44, pb.getTotalPage());
    }
}
