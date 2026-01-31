package ec.edu.espe.fleet_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI fleetServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8084");
        localServer.setDescription("Development Server");

        Server kongServer = new Server();
        kongServer.setUrl("http://localhost:8000/fleet");
        kongServer.setDescription("Kong Gateway");

        Contact contact = new Contact();
        contact.setName("LogiFlow Team");
        contact.setEmail("support@logiflow.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("LogiFlow Fleet Service API")
                .version("1.0.0")
                .description("API REST para gestión de repartidores y vehículos")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, kongServer));
    }
}
