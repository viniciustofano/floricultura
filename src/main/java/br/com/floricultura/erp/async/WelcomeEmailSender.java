package br.com.floricultura.erp.async;
import br.com.floricultura.erp.model.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class WelcomeEmailSender {
    @Async("taskExecutor")
    public void sendWelcomeEmail(Cliente cliente) {
        try {

            Thread.sleep(3000); // 3 segundos de atraso
            log.info("E-MAIL DE BOAS-VINDAS: E-mail enviado para {} (ID: {}) no endereço {}", cliente.getNome(),
                    cliente.getId(), cliente.getEmail());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Erro ao enviar e-mail de boas-vindas para {}: {}", cliente.getEmail(), e.getMessage());
        }
    }
}