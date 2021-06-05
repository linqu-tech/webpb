package tech.linqu.webpb.runtime.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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

class WepbRequestBodyAdviceTest {

    private MethodParameter getMethodParameter(WepbRequestBodyAdvice advice) {
        Method method = ReflectionUtils
            .findMethod(FooController.class, "getFoo", FooRequest.class)
            .orElse(null);
        assertNotNull(method);
        InvocableHandlerMethod handlerMethod = new InvocableHandlerMethod(advice, method);
        return handlerMethod.getMethodParameters()[0];
    }

    @Test
    void shouldSupportWebpbMessage() {
        WepbRequestBodyAdvice advice = new WepbRequestBodyAdvice();
        MethodParameter methodParameter = getMethodParameter(advice);
        assertTrue(advice.supports(methodParameter, mock(Type.class),
            ObjectToStringHttpMessageConverter.class));
    }

    @Test
    void shouldReturnOriginBodyWhenRequestIsNull() {
        WepbRequestBodyAdvice advice = new WepbRequestBodyAdvice();
        MethodParameter methodParameter = getMethodParameter(advice);
        FooRequest request = new FooRequest();
        FooRequest body = (FooRequest) advice
            .afterBodyRead(request, mock(HttpInputMessage.class), methodParameter, mock(Type.class),
                ObjectToStringHttpMessageConverter.class);
        assertEquals(request.getId(), body.getId());
    }

    @Test
    void shouldReturnOriginBodyWhenRequestWithoutParameters() {
        WepbRequestBodyAdvice advice = new WepbRequestBodyAdvice();
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
        WepbRequestBodyAdvice advice = new WepbRequestBodyAdvice();
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

    @Test
    void shouldUpdateRequestSuccessWhenWithParameters() {
        WepbRequestBodyAdvice advice = new WepbRequestBodyAdvice();
        MethodParameter methodParameter = getMethodParameter(advice);
        FooRequest request = new FooRequest();
        try (MockedStatic<RequestContextHolder> holder = mockStatic(RequestContextHolder.class)) {
            Map<String, String[]> parameters = new HashMap<>();
            parameters.put("id", new String[] { "12345678" });
            parameters.put("size", new String[] { "111" });
            parameters.put("page", new String[] { "222" });
            parameters.put("fake1", null);
            parameters.put("fake2", new String[0]);
            HttpServletRequest servletRequest = mock(HttpServletRequest.class);
            when(servletRequest.getParameterMap()).thenReturn(parameters);
            ServletRequestAttributes attributes = new ServletRequestAttributes(servletRequest);
            holder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);
            FooRequest body = (FooRequest) advice
                .afterBodyRead(request, mock(HttpInputMessage.class), methodParameter,
                    mock(Type.class), ObjectToStringHttpMessageConverter.class);
            assertEquals(12345678, body.getId());
            assertEquals(111, body.getPageable().getSize());
        }
    }

    @Test
    void shouldUpdateRequestSuccessWhenWithPathVariables() {
        WepbRequestBodyAdvice advice = new WepbRequestBodyAdvice();
        MethodParameter methodParameter = getMethodParameter(advice);
        FooRequest request = new FooRequest();
        try (MockedStatic<RequestContextHolder> holder = mockStatic(RequestContextHolder.class)) {
            Map<String, String> variables = new HashMap<>();
            variables.put("id", "12345678");
            variables.put("size", "111");
            variables.put("page", "222");
            HttpServletRequest servletRequest = mock(HttpServletRequest.class);
            when(servletRequest.getAttribute(any())).thenReturn(variables);
            ServletRequestAttributes attributes = new ServletRequestAttributes(servletRequest);
            holder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);
            FooRequest body = (FooRequest) advice
                .afterBodyRead(request, mock(HttpInputMessage.class), methodParameter,
                    mock(Type.class), ObjectToStringHttpMessageConverter.class);
            assertEquals(12345678, body.getId());
            assertEquals(111, body.getPageable().getSize());
        }
    }

    @Test
    void shouldThrowExceptionWhenObjectMapperErrors() {
        ObjectMapper objectMapper = spy(ObjectMapper.class);
        doThrow(new RuntimeException()).when(objectMapper).readerForUpdating(any());
        WepbRequestBodyAdvice advice = new WepbRequestBodyAdvice(objectMapper);
        MethodParameter methodParameter = getMethodParameter(advice);
        FooRequest request = new FooRequest();
        try (MockedStatic<RequestContextHolder> holder = mockStatic(RequestContextHolder.class)) {
            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            servletRequest.setParameter("id", "12345678");
            ServletRequestAttributes attributes = new ServletRequestAttributes(servletRequest);
            holder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);
            assertThrows(RuntimeException.class, () -> advice
                .afterBodyRead(request, mock(HttpInputMessage.class), methodParameter,
                    mock(Type.class), ObjectToStringHttpMessageConverter.class));
        }
    }
}
