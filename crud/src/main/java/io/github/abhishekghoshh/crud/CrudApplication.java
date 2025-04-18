package io.github.abhishekghoshh.crud;

import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.file.FileService;
import io.github.abhishekghoshh.crud.controller.filter.LoggingFilter;
import io.github.abhishekghoshh.crud.controller.routes.HealthController;
import io.github.abhishekghoshh.crud.controller.routes.UserController;
import io.github.abhishekghoshh.crud.neo4j.Neo4jRepository;
import io.github.abhishekghoshh.crud.repository.UserRepository;
import io.github.abhishekghoshh.crud.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrudApplication {
    private static final Logger logger = LogManager.getLogger(CrudApplication.class);

    public static void main(String[] args) {
        Server server = newServer(8080);
        server.closeOnJvmShutdown();
        server.start().join();
        logger.info("Server has been started at http://127.0.0.1:{}",
                server.activeLocalPort());
    }

    static Server newServer(int port) {
        ServerBuilder sb = Server.builder();
        DocService docService = DocService.builder()
                .build();
        FileService fileService = FileService.builder(ClassLoader.getSystemClassLoader(), "/static")
                .autoIndex(true) // Optional: enables directory listing
                .build();

        return sb.http(port)
                .serviceUnder("/docs", docService)
                .serviceUnder("/web", fileService)
                .annotatedService(healthController())
                .annotatedService(userController())
                .decorator(new LoggingFilter())
                .build();
    }

    private static HealthController healthController() {
        return new HealthController();
    }

    private static UserController userController() {
        Neo4jRepository neo4jRepository = new Neo4jRepository(
                "bolt://neo4j:7687",
                "neo4j",
                "neo4j-password",
                "neo4j",
                "io.github.abhishekghoshh.crud.model"
        ).applyMigrations()
                .build();
        UserRepository userRepository = new UserRepository(neo4jRepository);
        UserService userService = new UserService(userRepository);
        return new UserController(userService);
    }
}