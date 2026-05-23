package fiap.pethub;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
    info = @Info(
        title = "PetHub API — Java Backend",
        version = "1.1.0",
        description = "API responsável por Veterinários, Unidades Veterinárias, Pets, Consultas, Exames, Diagnósticos, Vacinas/Tratamentos, Pedidos Médicos e Leituras Wearable IoT.",
        contact = @Contact(
            name = "PetHub Team",
            url = "https://github.com/pethub"
        )
    )
)
@SpringBootApplication
@EnableScheduling
public class PethubApplication {

	public static void main(String[] args) {
		SpringApplication.run(PethubApplication.class, args);
	}

}
