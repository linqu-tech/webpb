package tech.linqu.webpb.utilities.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConstTest {

    @Test
    void shouldMatchWebpbOptionsFileSuccess() {
        assertFalse("WebpbOptions.".matches(Const.WEBPB_OPTIONS));
        assertFalse("WebpbOptions.prot".matches(Const.WEBPB_OPTIONS));
        assertFalse("aWebpbOptions.prot".matches(Const.WEBPB_OPTIONS));
        assertTrue("WEBPBOptions.proTo".matches(Const.WEBPB_OPTIONS));
        assertTrue("WebpbOptions.proto".matches(Const.WEBPB_OPTIONS));
        assertTrue("a/WebpbOptions.proto".matches(Const.WEBPB_OPTIONS));
        assertTrue("a/b/WebpbOptions.proto".matches(Const.WEBPB_OPTIONS));
        assertTrue("weBpb_optIons.proto".matches(Const.WEBPB_OPTIONS));
        assertTrue("webpb-options.proto".matches(Const.WEBPB_OPTIONS));
        assertTrue("webpb.options.proto".matches(Const.WEBPB_OPTIONS));
        assertTrue("webpb_options.proto".matches(Const.WEBPB_OPTIONS));
    }
}
