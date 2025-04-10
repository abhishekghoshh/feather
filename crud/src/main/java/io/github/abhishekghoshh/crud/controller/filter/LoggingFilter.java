package io.github.abhishekghoshh.crud.controller.filter;

import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.DecoratingHttpServiceFunction;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServiceRequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoggingFilter implements DecoratingHttpServiceFunction {
    private static final Logger logger = LogManager.getLogger(LoggingFilter.class);

    @Override
    public HttpResponse serve(HttpService delegate, ServiceRequestContext ctx, HttpRequest req) throws Exception {
        Map<String, String> context = new HashMap<>();
        String uuid = UUID.randomUUID().toString();
        context.put("uid", uuid);
        context.put("method", ctx.method().name());
        context.put("uri", ctx.path());
        MDC.setContextMap(context);

        logger.debug("Transaction started in OnePerRequestFilter");

        long startTime = System.currentTimeMillis();
        HttpResponse response = delegate.serve(ctx, req);
        long endTime = System.currentTimeMillis();

        response.whenComplete()
                .thenAccept(r -> {
                    MDC.clear();
                    logger.debug("clearing the MDC context for {}", uuid);
                    logger.info("ResponseTime={}ms|", (endTime - startTime));
                });
        return response;
    }
}
