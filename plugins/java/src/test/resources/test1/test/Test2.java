// Code generated by Webpb compiler, do not edit.
// https://github.com/linqu-tech/webpb
package test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbMeta;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Test2 implements WebpbMessage {

    public static final String WEBPB_METHOD = "GET";

    public static final String WEBPB_CONTEXT = "/test";

    public static final String WEBPB_PATH = "/test/{test2}?id={id}&data1={data.data1}&data2={data.data2}";

    public static final WebpbMeta WEBPB_META = new WebpbMeta.Builder().method(WEBPB_METHOD).context(WEBPB_CONTEXT).path(WEBPB_PATH).build();

    @Override
    public WebpbMeta webpbMeta() {
        return WEBPB_META;
    }

    private Long test2;

    private String id;

    private Data data;

    public Test2() {
    }

    public Test2(Long test2, String id, Data data) {
        this.test2 = test2;
        this.id = id;
        this.data = data;
    }
}
