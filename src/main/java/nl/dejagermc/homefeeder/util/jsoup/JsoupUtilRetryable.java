package nl.dejagermc.homefeeder.util.jsoup;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.IOException;
import java.util.Optional;

/**
 * Workaround for bug in Spring
 * https://stackoverflow.com/a/51062988
 *
 * Retryable only works if the method calling the retryable method is in a different class.
 */
public class JsoupUtilRetryable {

    private JsoupUtilRetryable() {
        // private
    }

    @Retryable(value = IOException.class, maxAttempts = 4, backoff = @Backoff(delay = 5000, multiplier = 1.5))
    static Optional<Document> getDocumentRetryable(Connection connection) throws IOException {
        return Optional.of(connection.get());
    }
}
