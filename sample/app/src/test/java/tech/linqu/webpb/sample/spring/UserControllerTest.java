/*
 * Copyright (c) 2020 linqu.tech, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.linqu.webpb.sample.spring;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tech.linqu.webpb.sample.proto.user.UserDataRequest;

class UserControllerTest {

    @Test
    void getUserMethodHasRequestMappingAnnotation() {
        Optional<Method> optional = ReflectionUtils
            .findMethod(UserController.class, "getUser", UserDataRequest.class);
        assertTrue(optional.isPresent());
        RequestMapping requestMapping = optional.get().getAnnotation(RequestMapping.class);
        assertArrayEquals(requestMapping.method(), new RequestMethod[] { RequestMethod.GET });
        assertArrayEquals(requestMapping.path(), new String[] { "/users/{id}" });
    }

    @Test
    void getUser2MethodHasRequestMappingAnnotation() {
        Optional<Method> optional = ReflectionUtils
            .findMethod(UserController.class, "getUser2", UserDataRequest.class);
        assertTrue(optional.isPresent());
        RequestMapping requestMapping = optional.get().getAnnotation(RequestMapping.class);
        assertArrayEquals(requestMapping.method(), new RequestMethod[] { RequestMethod.GET });
        assertArrayEquals(requestMapping.path(), new String[] { "/users/{id}" });
    }
}
