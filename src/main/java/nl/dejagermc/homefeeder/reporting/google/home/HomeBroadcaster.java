package nl.dejagermc.homefeeder.reporting.google.home;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class HomeBroadcaster {

    private static final String BROADCAST_JSON_MESSAGE = "{\"command\":\"%s\", \"user\":\"GoogleAssistantRelay\", \"broadcast\":\"false\"}";

    @Value("${google.assistant.relay.uri}")
    private String uri;

    public HomeBroadcaster() {
        // empty constructor, for now
    }

    public void broadcastMessage(String message) {
        String json = String.format(BROADCAST_JSON_MESSAGE, message);
        try {
            String response = Jsoup.connect(uri)
                    .timeout(60000)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/json")
                    .requestBody(json)
                    .execute()
                    .body();
            log.info("response: {}", response);
        } catch (IOException e) {
            log.error("Error bij broadcaste: {}", e);
        }
    }
}
