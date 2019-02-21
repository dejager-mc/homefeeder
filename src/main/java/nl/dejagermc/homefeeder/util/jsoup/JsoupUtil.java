package nl.dejagermc.homefeeder.util.jsoup;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class JsoupUtil {

    public Optional<Document> getDocument(final String uri) {
        try {
            return Optional.of(Jsoup.connect(uri).get());
        } catch (Exception e) {
            log.error("Error connecting to uri: {}", uri, e);
            return Optional.empty();
        }
    }
    public Optional<Document> getDocumentIgnoreContentType(final String uri) {
        try {
            return Optional.of(Jsoup.connect(uri).ignoreContentType(true).get());
        } catch (Exception e) {
            log.error("Error connecting to uri: {}", uri, e);
            return Optional.empty();
        }
    }

    public String postToOpenhab(String uri, String body) {
        try {
            return
                    Jsoup.connect(uri)
                            .timeout(5000)
                            .ignoreContentType(true)
                            .method(Connection.Method.POST)
                            .header("Content-Type", "text/plain")
                            .header("Accept", "application/json")
                            .requestBody(body)
                            .execute()
                            .body();
        } catch (IOException e) {
            log.error("Error connecting to uri: {}", uri, e);
            return "ERROR";
        }
    }
}
