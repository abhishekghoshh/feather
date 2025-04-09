package io.github.abhishekghoshh.crud.controller;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.annotation.Get;

import java.util.Map;

public class HealthController {
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
