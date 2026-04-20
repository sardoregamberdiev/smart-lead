package com.esardor.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartleadOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smartlead API")
                        .description(
                                "AI-powered lead qualification backend. " +
                                        "Inbound messages are automatically analyzed " +
                                        "by the HuggingFace Llama 3.3 model to " +
                                        "qualify genuine sales leads."
                        )
                        .version("v1")
                        .contact(
                                new Contact()
                                        .name("Sardor Egamberdiev")
                                        .url("https://www.linkedin.com/in/sardor-egamberdiev-4b131551/")
                        )
                );
    }
}
