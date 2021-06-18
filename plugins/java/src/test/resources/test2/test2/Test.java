// Code generated by Webpb compiler, do not edit.
// https://github.com/linqu-tech/webpb
package test2;

import java.beans.Transient;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbMeta;

public class Test implements WebpbMessage {

    public static final String WEBPB_METHOD = "";

    public static final String WEBPB_CONTEXT = "";

    public static final String WEBPB_PATH = "";

    public static final WebpbMeta WEBPB_META = new WebpbMeta.Builder().method(WEBPB_METHOD).context(WEBPB_CONTEXT).path(WEBPB_PATH).build();

    @Override
    public WebpbMeta webpbMeta() {
        return WEBPB_META;
    }

    private Integer test1;

    private Boolean test2;

    private Boolean isTest3;

    public Test() {
    }

    public Test(Integer test1, Boolean test2, Boolean isTest3) {
        this.test1 = test1;
        this.test2 = test2;
        this.isTest3 = isTest3;
    }

    public Integer getTest1() {
        return this.test1;
    }

    public Test setTest1(Integer test1) {
        this.test1 = test1;
        return this;
    }

    public Boolean getTest2() {
        return this.test2;
    }

    @Transient
    public boolean isTest2() {
        return this.test2 != null && this.test2;
    }

    public Test setTest2(Boolean test2) {
        this.test2 = test2;
        return this;
    }

    public Boolean getIsTest3() {
        return this.isTest3;
    }

    @Transient
    public boolean isIsTest3() {
        return this.isTest3 != null && this.isTest3;
    }

    public Test setIsTest3(Boolean isTest3) {
        this.isTest3 = isTest3;
        return this;
    }
}
