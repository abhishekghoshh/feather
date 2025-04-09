package io.github.abhishekghoshh.crud.controller.advide;

import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.ExceptionHandlerFunction;
import io.github.abhishekghoshh.crud.dto.InternalServerError;
import io.github.abhishekghoshh.crud.dto.ResourceError;
import io.github.abhishekghoshh.crud.exception.InternalServerException;
import io.github.abhishekghoshh.crud.exception.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler implements ExceptionHandlerFunction {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public HttpResponse handleException(ServiceRequestContext ctx, HttpRequest req, Throwable cause) {
        switch (cause) {
            case ResourceException resourceException -> {
                return HttpResponse.ofJson(
                        HttpStatus.valueOf(resourceException.getStatus().getId()),
                        ResourceError.create(resourceException)
                );
            }
            case InternalServerException internalServerException -> {
                return HttpResponse.ofJson(
                        HttpStatus.valueOf(internalServerException.getStatus().getId()),
                        InternalServerError.create(internalServerException)
                );
            }
            case IllegalArgumentException illegalArgumentException -> {
                return HttpResponse.ofJson(
                        HttpStatus.BAD_REQUEST,
                        InternalServerError.create(illegalArgumentException)
                );
            }
            default -> {
                return HttpResponse.ofJson(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        InternalServerError.create(cause)
                );
            }
        }
    }
}
