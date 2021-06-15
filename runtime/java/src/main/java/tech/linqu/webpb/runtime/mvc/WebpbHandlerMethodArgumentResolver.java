package tech.linqu.webpb.runtime.mvc;

import static tech.linqu.webpb.runtime.mvc.WebpbRequestUtils.mergeVariables;

import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbUtils;

/**
 * Resolve {@link WebpbMessage} argument without {@link RequestBody} annotation.
 */
public class WebpbHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return WebpbMessage.class.isAssignableFrom(parameter.getParameterType())
            && !parameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
        throws Exception {
        Object object = parameter.getParameterType().getDeclaredConstructor().newInstance();

        @SuppressWarnings("unchecked")
        Map<String, String> attributes = (Map<String, String>) webRequest
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0);
        Map<String, String[]> parameters = webRequest.getParameterMap();
        Map<String, String> variablesMap = mergeVariables(attributes, parameters);
        return WebpbUtils.updateMessage((WebpbMessage) object, variablesMap);
    }
}
