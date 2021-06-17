package tech.linqu.webpb.sample.proto.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(pb.isPagination());
        assertEquals(11, pb.getPage());
        assertEquals(22, pb.getSize());
        assertEquals("SORT", pb.getSort());
    }

    @Test
    void testIsPaginationSuccess() {
        assertTrue(new PageablePb().setPagination(true).isPagination());
        assertFalse(new PageablePb().isPagination());
        assertFalse(new PageablePb().setPagination(false).isPagination());
    }
}
