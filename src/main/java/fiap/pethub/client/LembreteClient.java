package fiap.pethub.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class LembreteClient {

    private final RestTemplate restTemplate;

    @Value("${pethub.dotnet.url:http://localhost:5000}")
    private String dotnetBaseUrl;

    public void criarLembrete(LembreteRequest request) {
        try {
            restTemplate.postForEntity(dotnetBaseUrl + "/lembretes", request, Void.class);
            log.info("Lembrete criado com sucesso para tutorId={} tipo={}", request.getTutorId(), request.getTipo());
        } catch (Exception ex) {
            log.warn("Falha ao criar lembrete no serviço C#: {}", ex.getMessage());
        }
    }
}

