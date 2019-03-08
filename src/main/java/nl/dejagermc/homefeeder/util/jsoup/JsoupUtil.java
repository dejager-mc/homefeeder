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

    private static final String ERROR_MSG = "Error connecting to uri: {}";

    public Optional<Document> getDocument(final String uri) {
        try {
            return Optional.of(Jsoup.connect(uri).timeout(5000).get());
        } catch (Exception e) {
            log.error(ERROR_MSG, uri);
            return Optional.empty();
        }
    }
    public Optional<Document> getDocumentIgnoreContentType(final String uri) {
        try {
            return Optional.of(Jsoup.connect(uri).timeout(5000).ignoreContentType(true).get());
        } catch (Exception e) {
            log.error(ERROR_MSG, uri);
            return Optional.empty();
        }
    }

    public String postJsonToOpenhab(String uri, String body) {
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
            log.error(String.format(ERROR_MSG, uri), e);
            return "ERROR";
        }
    }

    public String postJsonToGoogleRelayAssistant(String uri, String json) {
        try {
            return Jsoup.connect(uri)
                    .timeout(5000)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/json")
                    .requestBody(json)
                    .execute()
                    .body();
        } catch (IOException e) {
            log.error(String.format(ERROR_MSG, uri), e);
            return "ERROR";
        }
    }

    public Optional<Document> getPostNlDeliveriesDocument(String user, String password) {

        return Optional.empty();
    }
}
