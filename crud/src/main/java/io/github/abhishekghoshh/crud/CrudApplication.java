package io.github.abhishekghoshh.crud;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrudApplication {
    private static final Logger logger = LogManager.getLogger(CrudApplication.class);

    public static void main(String[] args) {
        Server server = newServer(8080);

        server.closeOnJvmShutdown();

        server.start().join();

        logger.info("Server has been started. Serving dummy service at http://127.0.0.1:{}",
                server.activeLocalPort());
    }

    static Server newServer(int port) {
        ServerBuilder sb = Server.builder();
        return sb.http(port)
                .service("/", (ctx, req) -> {
                    logger.info("Received request: {}", req);
                    return HttpResponse.of("Hello, Armeria!");
                })
                .build();
    }
}