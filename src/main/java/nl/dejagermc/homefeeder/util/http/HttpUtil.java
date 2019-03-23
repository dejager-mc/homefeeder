package nl.dejagermc.homefeeder.util.http;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class HttpUtil {

    private HttpUtilRetryable httpUtilRetryable;

    @Autowired
    public HttpUtil(HttpUtilRetryable httpUtilRetryable) {
        this.httpUtilRetryable = httpUtilRetryable;
    }

    private static final String ERROR_MSG = "Error connecting to uri: %s";

    @Cacheable(cacheNames = "getCachedDocument", cacheManager = "cacheManagerCaffeine")
    public Optional<Document> getCachedDocument(final String uri) {
        log.info("UC999: getCachedDocument: {}", uri);
        return getDocument(uri);
    }

    public Optional<Document> getDocument(final String uri) {
        log.info("UC999: getDocument: {}", uri);
        try {
            return httpUtilRetryable.getDocumentRetryable(Jsoup.connect(uri).timeout(5000));
        } catch (IOException e) {
            log.error(String.format(ERROR_MSG, uri), e);
            return Optional.empty();
        }
    }

    public Optional<Document> getDocumentIgnoreContentType(final String uri) {
        log.info("UC999: getDocumentIgnoreContentType: {}", uri);
        try {
            return httpUtilRetryable.getDocumentRetryable(Jsoup.connect(uri).timeout(5000).ignoreContentType(true));
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
}
