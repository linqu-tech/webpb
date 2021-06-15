package tech.linqu.webpb.runtime.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.MockedStatic;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.ObjectToStringHttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import tech.linqu.webpb.runtime.model.BadRequest;
import tech.linqu.webpb.runtime.model.FooController;
import tech.linqu.webpb.runtime.model.FooRequest;

class WebpbRequestBodyAdviceTest {

    private MethodParameter getMethodParameter(WebpbRequestBodyAdvice advice) {
        Method method = ReflectionUtils
            .findMethod(FooController.class, "getFoo", FooRequest.class)
            .orElse(null);
        assertNotNull(method);
        InvocableHandlerMethod handlerMethod = new InvocableHandlerMethod(advice, method);
        return handlerMethod.getMethodParameters()[0];
    }

    @Test
    void shouldSupportWebpbMessage() {
        WebpbRequestBodyAdvice advice = new WebpbRequestBodyAdvice();
        MethodParameter methodParameter = getMethodParameter(advice);
        assertTrue(advice.supports(methodParameter, mock(Type.class),
            ObjectToStringHttpMessageConverter.class));
    }

    @Test
    void shouldReturnOriginBodyWhenRequestIsNull() {
        WebpbRequestBodyAdvice advice = new WebpbRequestBodyAdvice();
        MethodParameter methodParameter = getMethodParameter(advice);
        FooRequest request = new FooRequest();
        FooRequest body = (FooRequest) advice
            .afterBodyRead(request, mock(HttpInputMessage.class), methodParameter, mock(Type.class),
                ObjectToStringHttpMessageConverter.class);
        assertEquals(request.getId(), body.getId());
    }

    @Test
    void shouldReturnOriginBodyWhenRequestWithoutParameters() {
        WebpbRequestBodyAdvice advice = new WebpbRequestBodyAdvice();
        MethodParameter methodParameter = getMethodParameter(advice);
        BadRequest request = new BadRequest();
        try (MockedStatic<RequestContextHolder> holder = mockStatic(RequestContextHolder.class)) {
            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            ServletRequestAttributes attributes = new ServletRequestAttributes(servletRequest);
            holder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);
            Object body =
                advice.afterBodyRead(request, mock(HttpInputMessage.class), methodParameter,
                    mock(Type.class), ObjectToStringHttpMessageConverter.class);
            assertEquals(request, body);
        }
    }

    @Test
    void shouldReturnOriginBodyWhenRequestWithoutWepebMeta() {
        WebpbRequestBodyAdvice advice = new WebpbRequestBodyAdvice();
        MethodParameter methodParameter = getMethodParameter(advice);
        BadRequest request = new BadRequest();
        try (MockedStatic<RequestContextHolder> holder = mockStatic(RequestContextHolder.class)) {
            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            servletRequest.setParameter("id", "12345678");
            servletRequest.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
                Collections.emptyMap());
            ServletRequestAttributes attributes = new ServletRequestAttributes(servletRequest);
            holder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);
            Object body =
                advice.afterBodyRead(request, mock(HttpInputMessage.class), methodParameter,
                    mock(Type.class), ObjectToStringHttpMessageConverter.class);
            assertEquals(request, body);
        }
    }
}
