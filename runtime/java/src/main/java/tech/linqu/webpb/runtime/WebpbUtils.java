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
package tech.linqu.webpb.runtime;

import org.springframework.beans.FatalBeanException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tech.linqu.webpb.runtime.messaging.WebpbMessagingMapping;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;

/**
 * WebpbUtils
 */
public class WebpbUtils {

    /**
     * updateAnnotation
     * @param method Method
     */
    @SuppressWarnings("unchecked")
    public static void updateAnnotation(Method method) {
        if (!method.isAnnotationPresent(WebpbRequestMapping.class)) {
            return;
        }
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        assert requestMapping != null;
        WebpbRequestMapping webpbRequestMapping = method.getAnnotation(WebpbRequestMapping.class);

        Class<?> clazz = webpbRequestMapping.message();
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            for (Class<?> parameterType : method.getParameterTypes()) {
                if (WebpbMessage.class.isAssignableFrom(parameterType)) {
                    clazz = parameterType;
                }
            }
        }
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            throw new FatalBeanException(WebpbRequestMapping.class.getSimpleName()
                + " message is not specified");
        }
        WebpbMeta meta = Objects.requireNonNull(WebpbUtils.readWebpbMeta(clazz),
            WebpbMessagingMapping.class.getSimpleName() + " 'MESSAGE_META' is required");

        String methodName = Objects.requireNonNull(meta.getMethod(),
            WebpbRequestMapping.class.getSimpleName() + " 'METHOD' is required");
        String path = Objects.requireNonNull(meta.getPath(),
            WebpbRequestMapping.class.getSimpleName() + " 'PATH' is required");

        Map<Class<? extends Annotation>, Annotation> annotations;
        try {
            Field field = Executable.class.getDeclaredField("declaredAnnotations");
            field.setAccessible(true);
            annotations = (Map<Class<? extends Annotation>, Annotation>) field.get(method);
        } catch (Exception ignored) {
            throw new FatalBeanException("Read annotations error");
        }
        AnnotationUtils.clearCache();
        annotations.remove(WebpbRequestMapping.class);
        Class<? extends Annotation> annotationType = requestMapping.annotationType();
        String mappingName = requestMapping.name();
        String[] mappingValue = requestMapping.value();
        String[] mappingPath = new String[] { path.split("\\?")[0] };
        RequestMethod[] mappingMethod = new RequestMethod[] {
            RequestMethod.valueOf(methodName.toUpperCase())
        };
        String[] mappingParams = requestMapping.params();
        String[] mappingHeaders = requestMapping.headers();
        String[] mappingConsumes = requestMapping.consumes();
        String[] mappingProduces = requestMapping.produces();
        annotations.put(RequestMapping.class, new RequestMapping() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return annotationType;
            }

            @Override
            public String name() {
                return mappingName;
            }

            @Override
            public String[] value() {
                return mappingValue;
            }

            @Override
            public String[] path() {
                return mappingPath;
            }

            @Override
            public RequestMethod[] method() {
                return mappingMethod;
            }

            @Override
            public String[] params() {
                return mappingParams;
            }

            @Override
            public String[] headers() {
                return mappingHeaders;
            }

            @Override
            public String[] consumes() {
                return mappingConsumes;
            }

            @Override
            public String[] produces() {
                return mappingProduces;
            }
        });
    }

    /**
     * readWebpbMeta
     * @param type Class
     * @return WebpbMeta
     */
    public static WebpbMeta readWebpbMeta(Class<?> type) {
        try {
            Field field = type.getDeclaredField("WEBPB_META");
            return (WebpbMeta) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
