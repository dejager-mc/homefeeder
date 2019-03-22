package nl.dejagermc.homefeeder.output.google.home;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GoogleHomeOutputService {

    private static final String BROADCAST_JSON_MESSAGE = "{\"command\":\"%s\", \"user\":\"GoogleAssistantRelay\", \"broadcast\":\"false\"}";

    @Value("${google.assistant.relay.uri}")
    private String uri;

    private HttpUtil httpUtil;

    @Autowired
    public GoogleHomeOutputService(HttpUtil httpUtil) {
        this.httpUtil = httpUtil;
    }

    public void broadcast(List<String> messages) {
        messages.forEach(this::broadcast);
    }

    public void broadcast(String message) {
        if (message.isBlank()) {
            return;
        }

        log.info("UC002: report to google home.");

        String optimisedMessage = optimiseNamesForSpeech(message);
        String json = String.format(BROADCAST_JSON_MESSAGE, optimisedMessage);
        broadcastToGoogleHome(json);
    }

    private String optimiseNamesForSpeech(String message) {
        return message
                .replaceAll("OG", "O G ")
                .replaceAll("VP", "V P ")
                .replaceAll("NIP", "N I P ")
                .replaceAll("EG", "E G ");
    }

    private void broadcastToGoogleHome(String json) {
        String response = httpUtil.postJsonToGoogleRelayAssistant(uri, json);
        handleResponse(response);
    }

    private void handleResponse(String response) {
        if (response.matches(".*\"success\":true.*")) {
            log.info("UC002: successful.");
        } else {
            log.error("UC002: Error: {}", response);
        }
    }
}
