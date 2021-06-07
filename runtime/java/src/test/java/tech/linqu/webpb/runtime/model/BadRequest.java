package tech.linqu.webpb.runtime.model;

import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbMeta;

/**
 * A bad test class implements {@link WebpbMessage}.
 */
public class BadRequest implements WebpbMessage {

    @Override
    public WebpbMeta webpbMeta() {
        return null;
    }
}
