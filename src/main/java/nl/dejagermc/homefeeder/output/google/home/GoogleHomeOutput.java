package nl.dejagermc.homefeeder.output.google.home;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GoogleHomeOutput {

    private static final String BROADCAST_JSON_MESSAGE = "{\"command\":\"%s\", \"user\":\"GoogleAssistantRelay\", \"broadcast\":\"false\"}";

    @Value("${google.assistant.relay.uri}")
    private String uri;

    private JsoupUtil jsoupUtil;

    @Autowired
    public GoogleHomeOutput(JsoupUtil jsoupUtil) {
        this.jsoupUtil = jsoupUtil;
    }

    public void broadcast(List<String> messages) {
        messages.forEach(this::broadcast);
    }

    public void broadcast(String message) {
        if (message.isBlank()) {
            return;
        }

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
        log.info("Broadcasting: {}", json);
        String response = jsoupUtil.postJsonToGoogleRelayAssistant(uri, json);
        handleResponse(response);
    }

    private void handleResponse(String response) {
        if (response.matches(".*\"success\":true.*")) {
            log.info("Google home broadcast: ok");
        } else {
            log.error("Google home broadcast: not ok: {}", response);
        }
    }
}
