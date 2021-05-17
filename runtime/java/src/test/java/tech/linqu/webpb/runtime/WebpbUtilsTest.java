package tech.linqu.webpb.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebpbUtilsTest {

    static class MockMessage implements WebpbMessage {

        public static final WebpbMeta WEBPB_META = new WebpbMeta();

        @Override
        public WebpbMeta webpbMeta() {
            return WEBPB_META;
        }
    }

    @Test
    void shouldReadWebpbMetaSuccess() {
        WebpbMeta webpbMeta = WebpbUtils.readWebpbMeta(MockMessage.class);
        assertNotNull(webpbMeta);
    }
}
