package nl.dejagermc.homefeeder.output.google.home;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class GoogleHomeOutput {

    private static final String BROADCAST_JSON_MESSAGE = "{\"command\":\"%s\", \"user\":\"GoogleAssistantRelay\", \"broadcast\":\"false\"}";

    @Value("${google.assistant.relay.uri}")
    private String uri;

    @Autowired
    public GoogleHomeOutput() {
        // empty
    }

    public boolean broadcast(String message) {
        if (message.isBlank()) {
            return true;
        }

        String optimisedMessage = optimiseNamesForSpeech(message);
        String json = String.format(BROADCAST_JSON_MESSAGE, optimisedMessage);
        return broadcastToGoogleHome(json);
    }

    private String optimiseNamesForSpeech(String message) {
        return message
                .replaceAll("OG", "O G ")
                .replaceAll("VP", "V P ")
                .replaceAll("NIP", "N I P ")
                .replaceAll("EG", "E G ");
    }

    private boolean broadcastToGoogleHome(String json) {
        log.info("Broadcasting: {}", json);
        try {
            String response = Jsoup.connect(uri)
                    .timeout(60000)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/json")
                    .requestBody(json)
                    .execute()
                    .body();
            handleResponse(response);
            return true;
        } catch (IOException e) {
            log.error("Error sending broadcast: {}", e);
            return false;
        }
    }

    private void handleResponse(String response) {
        if (response.matches(".*\"success\":true.*")) {
            log.info("Google home broadcast: ok");
        } else {
            log.error("Google home broadcast: not ok: {}", response);
        }
    }
}
