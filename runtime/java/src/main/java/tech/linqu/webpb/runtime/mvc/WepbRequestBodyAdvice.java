package tech.linqu.webpb.runtime.mvc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Type;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import tech.linqu.webpb.commons.ParamGroup;
import tech.linqu.webpb.commons.PathParam;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbMeta;
import tech.linqu.webpb.runtime.WebpbUtils;

/**
 * Autowire request body properties from url path an query.
 */
@RestControllerAdvice
public class WepbRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private final ObjectMapper objectMapper;

    /**
     * Constructor.
     */
    public WepbRequestBodyAdvice() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return WebpbMessage.class.isAssignableFrom(methodParameter.getParameterType());
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
                                MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        Object object =
            super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return object;
        }

        @SuppressWarnings("unchecked")
        Map<String, String> variablesMap = (Map<String, String>) request
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length > 0) {
                variablesMap.put(entry.getKey(), entry.getValue()[0]);
            }
        }

        @SuppressWarnings("unchecked")
        Class<? extends WebpbMessage> clazz = (Class<? extends WebpbMessage>) object.getClass();
        WebpbMeta meta = WebpbUtils.readWebpbMeta(clazz);
        if (meta == null) {
            return object;
        }
        ParamGroup group = ParamGroup.of(meta.getPath());
        ObjectNode objectNode = objectMapper.createObjectNode();
        for (PathParam pathParam : group.getParams()) {
            String key = pathParam.getKey();
            String accessor = pathParam.getAccessor();
            String value = variablesMap.get(StringUtils.hasLength(key) ? key : accessor);
            if (value != null) {
                String[] accessors = accessor.split("\\.");
                ObjectNode targetNode = findNode(objectNode, accessors);
                targetNode.put(accessors[accessors.length - 1], value);
            }
        }
        try {
            return objectMapper.readerForUpdating(object).readValue(objectNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    private ObjectNode findNode(ObjectNode objectNode, String[] accessors) {
        for (int i = 0; i < accessors.length - 1; i++) {
            String accessor = accessors[i];
            ObjectNode subNode = (ObjectNode) objectNode.get(accessor);
            if (subNode == null) {
                subNode = objectMapper.createObjectNode();
                objectNode.set(accessor, subNode);
            }
            objectNode = subNode;
        }
        return objectNode;
    }
}
