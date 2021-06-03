package tech.linqu.webpb.sample.spring;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;
import tech.linqu.webpb.sample.proto.user.UserDataRequest;

/**
 * User controller.
 */
public class UserController {

    /**
     * Get user.
     *
     * @param request {@link UserDataRequest}
     * @return {@link Long}
     */
    @WebpbRequestMapping(message = UserDataRequest.class)
    public Long getUser(@Valid @RequestBody UserDataRequest request) {
        return request.getId();
    }

    /**
     * Get user.
     *
     * @param request {@link UserDataRequest}
     * @return {@link Long}
     */
    @WebpbRequestMapping
    public Long getUser2(@Valid @RequestBody UserDataRequest request) {
        return request.getId();
    }
}
