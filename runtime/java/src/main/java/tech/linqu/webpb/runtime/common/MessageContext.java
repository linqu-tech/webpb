package tech.linqu.webpb.runtime.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;
import tech.linqu.webpb.commons.SegmentGroup;

/**
 * Context for webpb message.
 */
@Accessors(chain = true)
@Getter
@Setter
public class MessageContext {

    /**
     * To test if a context is null, and use for cache.
     */
    public static final MessageContext NULL_CONTEXT = new MessageContext();

    HttpMethod method;

    String context;

    String path;

    SegmentGroup segmentGroup;
}
