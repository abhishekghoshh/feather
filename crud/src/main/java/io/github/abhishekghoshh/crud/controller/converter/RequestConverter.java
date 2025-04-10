package io.github.abhishekghoshh.crud.controller.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.armeria.common.AggregatedHttpRequest;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.RequestConverterFunction;

import java.lang.reflect.ParameterizedType;

public abstract class RequestConverter<T> implements RequestConverterFunction {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<T> classType;

    @SuppressWarnings("unchecked")
    protected RequestConverter() {
        this.classType = (Class<T>) extractTypeParameter(getClass());
    }

    @Override
    public Object convertRequest(
            ServiceRequestContext ctx,
            AggregatedHttpRequest request,
            Class<?> expectedResultType,
            ParameterizedType expectedParameterizedResultType
    ) throws Exception {
        if (expectedResultType.equals(classType)) {
            return mapper.readValue(request.contentUtf8(), classType);
        }
        return RequestConverterFunction.fallthrough();
    }

    // Helper to get T from RequestConverter<T>
    private static Class<?> extractTypeParameter(Class<?> clazz) {
        while (clazz != null) {
            if (clazz.getGenericSuperclass() instanceof ParameterizedType parameterizedType) {
                if (parameterizedType.getActualTypeArguments()[0] instanceof Class<?> typeArg) {
                    return typeArg;
                }
            }
            clazz = clazz.getSuperclass();
        }
        throw new IllegalStateException("Unable to determine type parameter T.");
    }
}