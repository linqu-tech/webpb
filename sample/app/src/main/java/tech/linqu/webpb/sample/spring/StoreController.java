package tech.linqu.webpb.sample.spring;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;
import tech.linqu.webpb.sample.proto.store.StoreRequest;

public class StoreController {

    @WebpbRequestMapping(message = StoreRequest.class)
    public Integer getStore(@Valid @RequestBody StoreRequest request) {
        return request.getId();
    }
}
