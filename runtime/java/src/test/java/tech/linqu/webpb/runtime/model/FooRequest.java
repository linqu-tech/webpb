package tech.linqu.webpb.runtime.model;

import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbMeta;
import tech.linqu.webpb.runtime.common.InQuery;

/**
 * Test class implements {@link WebpbMessage}.
 */
public class FooRequest implements WebpbMessage {

    @Override
    public WebpbMeta webpbMeta() {
        return new WebpbMeta.Builder()
            .method("POST")
            .path("https://domain/{id}/action?size={pageSize}&page={pageNo}")
            .build();
    }

    @InQuery
    private int id = 123;

    @InQuery
    private int pageSize = 234;

    @InQuery
    private int pageNo = 345;

    private String data = "Hello, world!";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
