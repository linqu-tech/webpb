// Code generated by Webpb compiler, do not edit.
// https://github.com/linqu-tech/webpb
package test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbMeta;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data implements WebpbMessage {

    public static final String WEBPB_METHOD = "";

    public static final String WEBPB_CONTEXT = "";

    public static final String WEBPB_PATH = "";

    public static final WebpbMeta WEBPB_META = new WebpbMeta.Builder().method(WEBPB_METHOD).context(WEBPB_CONTEXT).path(WEBPB_PATH).build();

    @Override
    public WebpbMeta webpbMeta() {
        return WEBPB_META;
    }

    private String data1;

    private Integer data2;

    public Data() {
    }

    public Data(String data1, Integer data2) {
        this.data1 = data1;
        this.data2 = data2;
    }
}