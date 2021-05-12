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
package tech.linqu.webpb.runtime.messaging;

import tech.linqu.webpb.runtime.WebpbMeta;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.messaging.handler.CompositeMessageCondition;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.rsocket.annotation.support.RSocketFrameTypeMessageCondition;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * WebpbRSocketMessageHandler
 */
public class WebpbRSocketMessageHandler extends RSocketMessageHandler {

    @Override
    protected CompositeMessageCondition getCondition(AnnotatedElement element) {
        WebpbMessagingMapping annotation =
            AnnotatedElementUtils.findMergedAnnotation(element, WebpbMessagingMapping.class);
        if (annotation != null) {
            Class<?> clazz = null;
            if (annotation.message() != WebpbMessage.class) {
                clazz = annotation.message();
            } else {
                for (Class<?> parameterType : ((Method) element).getParameterTypes()) {
                    if (WebpbMessage.class.isAssignableFrom(parameterType)) {
                        clazz = parameterType;
                    }
                }
            }
            if (clazz == null) {
                throw new FatalBeanException(WebpbMessagingMapping.class.getSimpleName()
                    + " message class not specified");
            }
            WebpbMeta meta = Objects.requireNonNull(WebpbUtils.readWebpbMeta(clazz),
                WebpbMessagingMapping.class.getSimpleName() + " 'MESSAGE_META' is required");
            if (!StringUtils.hasLength(meta.getPath())) {
                throw new NullPointerException("'path' is required");
            }
            return new CompositeMessageCondition(
                RSocketFrameTypeMessageCondition.EMPTY_CONDITION,
                new DestinationPatternsMessageCondition(
                    processDestinations(new String[] { meta.getPath() }), obtainRouteMatcher()
                ));
        }
        return super.getCondition(element);
    }
}
