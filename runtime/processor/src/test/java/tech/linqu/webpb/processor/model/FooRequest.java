package tech.linqu.webpb.processor.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbMeta;
import tech.linqu.webpb.runtime.common.InQuery;

/**
 * Test class implements {@link WebpbMessage}.
 */
@Accessors(chain = true)
@Getter
@Setter
public class FooRequest implements WebpbMessage {

    public static final String WEBPB_METHOD = "GET";

    public static final String WEBPB_PATH = "/foo";

    public static final WebpbMeta WEBPB_META =
        WebpbMeta.builder().method(WEBPB_METHOD).path(WEBPB_PATH).build();

    @Override
    public WebpbMeta webpbMeta() {
        return WEBPB_META;
    }

    @InQuery
    private int id = 123;

    @InQuery
    private String index = "index123";

    private String data = "data123";
}
