package com.dreamsol.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "DreamSol Tele Solutions Pvt Ltd",
                version = "v3",
                description = "API documentation for VAM Project"
        )
)
public class SwaggerConfig {
   //  Configuration class for swagger, if any further customization is needed
}
