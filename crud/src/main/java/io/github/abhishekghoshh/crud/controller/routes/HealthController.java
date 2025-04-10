package io.github.abhishekghoshh.crud.controller.routes;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.annotation.Get;

import java.util.Map;

/**
 * HealthController is a controller class that handles health check requests.
 * It provides an endpoint to check the health status of the application.
 */
public class HealthController {

    /**
     * Handles the health check request.
     *
     * @return HttpResponse with health status information.
     */
    @Get("/health")
    public HttpResponse health() {
        return HttpResponse.ofJson(HttpStatus.OK,
                Map.of(
                        "status", "UP",
                        "version", "0.0.1",
                        "timestamp", System.currentTimeMillis()
                ));
    }
}
