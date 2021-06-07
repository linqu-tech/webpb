package tech.linqu.webpb.runtime.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public static final WebpbMeta WEBPB_META = WebpbMeta.builder().build();

    private WebpbMeta webpbMeta = WebpbMeta.builder()
        .method("POST")
        .path(
            "/domain/{id}/action?pagination={pagination}&size={pageable.size}&page={pageable.page}")
        .build();

    @Override
    public WebpbMeta webpbMeta() {
        return webpbMeta;
    }

    public FooRequest() {
    }

    public FooRequest(WebpbMeta webpbMeta) {
        this.webpbMeta = webpbMeta;
    }

    @InQuery
    private int id = 123;

    @InQuery
    private boolean pagination = true;

    @InQuery
    private Pageable pageable = new Pageable().setPage(10).setSize(20);

    @JsonIgnore
    private String ignored = "IGNORED";

    private String data = "data123";
}
