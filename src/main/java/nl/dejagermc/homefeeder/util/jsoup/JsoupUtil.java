package nl.dejagermc.homefeeder.util.jsoup;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static nl.dejagermc.homefeeder.util.jsoup.JsoupUtilRetryable.getDocumentRetryable;

@Component
@Slf4j
public class JsoupUtil {

    private static final String ERROR_MSG = "Error connecting to uri: %s";

    public Optional<Document> getDocument(final String uri) {
        try {
            return getDocumentRetryable(Jsoup.connect(uri).timeout(5000));
        } catch (IOException e) {
            log.error(String.format(ERROR_MSG, uri), e);
            return Optional.empty();
        }
    }

    public Optional<Document> getDocumentIgnoreContentType(final String uri) {
        try {
            return getDocumentRetryable(Jsoup.connect(uri).timeout(5000).ignoreContentType(true));
        } catch (IOException e) {
            log.error(String.format(ERROR_MSG, uri), e.getMessage());
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
            log.error(String.format(ERROR_MSG, uri),e.getMessage());
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
            log.error(String.format(ERROR_MSG, uri), e.getMessage());
            return "ERROR";
        }
    }

    public Optional<Document> getPostNlDeliveriesDocument(String user, String password) {

        return Optional.empty();
    }
}
