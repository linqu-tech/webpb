package tech.linqu.webpb.runtime.mvc;

import static tech.linqu.webpb.runtime.mvc.WebpbRequestUtils.mergeVariables;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbUtils;

/**
 * Autowire request body properties from url path an query.
 */
@RestControllerAdvice
public class WebpbRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private final ObjectMapper objectMapper;

    /**
     * Construct an instance of {@link WebpbRequestBodyAdvice}.
     */
    public WebpbRequestBodyAdvice() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Construct an instance of {@link WebpbRequestBodyAdvice}.
     *
     * @param objectMapper {@link ObjectMapper}
     */
    public WebpbRequestBodyAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
        Map<String, String> attributes = (Map<String, String>) request
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Map<String, String[]> parameters = request.getParameterMap();
        Map<String, String> variablesMap = mergeVariables(attributes, parameters);
        return WebpbUtils.updateMessage((WebpbMessage) object, variablesMap);
    }

    private static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }
}
