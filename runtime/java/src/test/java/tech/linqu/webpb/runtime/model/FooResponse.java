package tech.linqu.webpb.runtime.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbMeta;

/**
 * {@link FooResponse}.
 */
@Accessors(chain = true)
@Getter
@Setter
public class FooResponse implements WebpbMessage {

    private Long id;

    /**
     * Get {@link WebpbMeta}.
     *
     * @return {@link WebpbMeta}
     */
    @Override
    public WebpbMeta webpbMeta() {
        return null;
    }
}
