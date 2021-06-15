package tech.linqu.webpb.sample.proto.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class PageablePbTest {

    @Test
    void test() {
        PageablePb pb = new PageablePb()
            .setPagination(true)
            .setPage(11)
            .setSize(22)
            .setSort("SORT");
        assertNotNull(pb.webpbMeta());
        assertEquals(true, pb.getPagination());
        assertEquals(11, pb.getPage());
        assertEquals(22, pb.getSize());
        assertEquals("SORT", pb.getSort());
    }
}
