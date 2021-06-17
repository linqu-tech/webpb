package tech.linqu.webpb.runtime.mvc;

import static tech.linqu.webpb.runtime.mvc.WebpbRequestUtils.mergeVariables;

import java.util.Collections;
import java.util.Map;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbUtils;

/**
 * Resolve {@link WebpbMessage} argument without {@link RequestBody} annotation.
 */
public class WebpbHandlerMethodArgumentResolver
    extends AbstractMessageConverterMethodArgumentResolver {

    public WebpbHandlerMethodArgumentResolver() {
        super(Collections.singletonList(new ByteArrayHttpMessageConverter()));
    }

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
        Object arg = WebpbUtils.updateMessage((WebpbMessage) object, variablesMap);

        if (binderFactory != null) {
            String name = Conventions.getVariableNameForParameter(parameter);
            WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
            if (arg != null) {
                validateIfApplicable(binder, parameter);
                if (binder.getBindingResult().hasErrors()
                    && isBindExceptionRequired(binder, parameter)) {
                    throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
                }
            }
            if (mavContainer != null) {
                mavContainer
                    .addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
            }
        }
        return adaptArgumentIfNecessary(arg, parameter);
    }
}
