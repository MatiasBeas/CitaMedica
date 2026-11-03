package CasoHospital.Cita_medica.webclient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
public class BonoClient {

    private final WebClient webClient;

    public BonoClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8185/api/bono").build();
    }

    public Map<String, Object> obtenerBono(Long folio) {
        return webClient.get()
                .uri("/folio/{folio}", folio)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
