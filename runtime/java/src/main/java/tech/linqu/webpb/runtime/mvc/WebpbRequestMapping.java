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
package tech.linqu.webpb.runtime.mvc;

import tech.linqu.webpb.runtime.WebpbMessage;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * WebpbRequestMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
public @interface WebpbRequestMapping {

    /**
     * name
     *
     * @return String
     */
    @AliasFor(annotation = RequestMapping.class)
    String name() default "";

    /**
     * value
     *
     * @return String[]
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};

    /**
     * params
     *
     * @return String[]
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] params() default {};

    /**
     * headers
     *
     * @return String[]
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] headers() default {};

    /**
     * consumes
     *
     * @return String[]
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] consumes() default {};

    /**
     * produces
     *
     * @return String[]
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] produces() default {};

    /**
     * message
     *
     * @return Class
     */
    Class<? extends WebpbMessage> message() default WebpbMessage.class;
}
