package nl.dejagermc.homefeeder.output.google.home;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.user.UserState;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
@Slf4j
public class GoogleHomeReporter {

    private static final String BROADCAST_JSON_MESSAGE = "{\"command\":\"%s\", \"user\":\"GoogleAssistantRelay\", \"broadcast\":\"false\"}";
    private UserState userState;

    @Value("${google.assistant.relay.uri}")
    private String uri;

    @Autowired
    public GoogleHomeReporter(UserState userState) {
        this.userState = userState;
    }

    public void broadcast(String message) {
        if (userState.useGoogleHome()) {
            String optimisedMessage = optimiseNamesForSpeech(message);
            String json = String.format(BROADCAST_JSON_MESSAGE, optimisedMessage);
            broadcastToGoogleHome(json);
        }
    }

    private String optimiseNamesForSpeech(String message) {
        message = message.replaceAll("OG", "O G ");
        message = message.replaceAll("VP", "V P ");
        message = message.replaceAll("NIP", "N I P ");
        message = message.replaceAll("EG", "E G ");
        return message;
    }

    private void broadcastToGoogleHome(String json) {
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
        } catch (IOException e) {
            log.error("Error sending broadcast: {}", e);
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
