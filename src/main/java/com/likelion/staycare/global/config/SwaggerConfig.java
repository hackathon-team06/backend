package com.likelion.staycare.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl(contextPath);
        localServer.setDescription("Local Server");

        return new OpenAPI()
                .addServersItem(localServer)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(
                        new Components().addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("Stay-care API 명세")
                        .version("1.0")
                        .description("Swagger"));
    }

    @Bean
    public GroupedOpenApi customGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenApiCustomizer missionPathOrderingCustomizer() {
        List<String> orderedPaths = List.of(
                "/api/missions/morning-routine/options",
                "/api/missions/morning-routine/survey",
                "/api/missions/morning-routine/recommendations",
                "/api/missions/morning-routine",
                "/api/missions/morning",
                "/api/missions/steps/{stepId}",
                "/api/missions/morning-routine/items/{itemId}",
                "/api/missions/evening",
                "/api/missions/today",
                "/api/missions/options",
                "/api/missions/{missionId}/steps",
                "/api/missions/date"
        );

        return openApi -> {
            if (openApi.getPaths() == null || openApi.getPaths().isEmpty()) {
                return;
            }

            Paths existingPaths = new Paths();
            existingPaths.putAll(openApi.getPaths());

            Paths ordered = new Paths();
            for (String path : orderedPaths) {
                PathItem pathItem = existingPaths.remove(path);
                if (pathItem != null) {
                    ordered.addPathItem(path, pathItem);
                }
            }

            existingPaths.forEach(ordered::addPathItem);
            openApi.setPaths(ordered);
        };
    }
}
