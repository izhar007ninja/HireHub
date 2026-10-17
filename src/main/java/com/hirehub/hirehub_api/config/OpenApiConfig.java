package com.hirehub.hirehub_api.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME="Bearer Authentication";

    @Bean
    public OpenAPI hireHubOpenAPI(){
        return new OpenAPI()
                .info(
                new Info().title("HireHub Recruitment Platform API")
                        .version("1.0.0")
                        .description("Production-grade RESTful API for job postings, candidate applications, profile management, and recruitment workflows.")
                        .contact(
                                new Contact()
                                        .email("khanizhar1235@gmail.com")
                                        .name("HireHub Recruitment API")
                        )
                        .license(new License()
                                        .name("Apache 2.0")
                                        .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local Development Server")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter you jwt token")
                        )
                );
    }

}
