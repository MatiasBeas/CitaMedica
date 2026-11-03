package CasoHospital.Cita_medica.confi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .addServersItem(new Server().url("http://localhost:8500"))
                .info(new Info()
                        .title("API de Citas Médicas")
                        .version("1.0.0")
                        .description("Documentación para Microservicio de Citas Médicas") // Ajustado
                        .contact(new Contact()
                                .name("Equipo técnico")
                                .email("comunicaciones.hgf@redsalud.gob.cl")
                                .url("https://www.hospitalfricke.cl/"))
                        .license(new License()
                                .name("Hospital Viña del Mar")
                                .url("https://www.hospitalfricke.cl/pacientes")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}

