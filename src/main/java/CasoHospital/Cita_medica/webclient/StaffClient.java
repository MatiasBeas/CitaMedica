package CasoHospital.Cita_medica.webclient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
public class StaffClient {

    private final WebClient webClient;

    public StaffClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8182/api/staff").build();
    }

    public Map<String, Object> obtenerStaff(Long numRegistro) {
        return webClient.get()
                .uri("/nro_registro/{nro_re}", numRegistro)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}