package nl.dejagermc.homefeeder.util.jsoup;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class JsoupUtil {

    public Optional<Document> getDocument(String uri) {
        try {
            return Optional.of(Jsoup.connect(uri).get());
        } catch (Exception e) {
            log.error("Error connecting to uri: {}", uri, e);
            return Optional.empty();
        }
    }
    public Optional<Document> getDocumentIgnoreContentType(String uri) {
        try {
            return Optional.of(Jsoup.connect(uri).ignoreContentType(true).get());
        } catch (Exception e) {
            log.error("Error connecting to uri: {}", uri, e);
            return Optional.empty();
        }
    }
}
