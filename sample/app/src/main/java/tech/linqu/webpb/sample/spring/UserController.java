package tech.linqu.webpb.sample.spring;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;
import tech.linqu.webpb.sample.proto.user.UserDataRequest;

public class UserController {

    @WebpbRequestMapping(message = UserDataRequest.class)
    public Long getUser(@Valid @RequestBody UserDataRequest request) {
        return request.getId();
    }
}
