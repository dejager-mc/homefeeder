package nl.dejagermc.homefeeder.output.openhab;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class OpenhabOutput {

    private static final String ON = "ON";
    private static final String OFF = "OFF";

    @Value("${openhab.rest}")
    private String openhabApiUri;

    @Value("${openhab.tv.switch}")
    private String tvSwitch;

    @Value("${openhab.tv.stream}")
    private String tvStream;

    public void turnOnTv() {
        turnOnOpenhabItem(tvSwitch, ON);
    }

    public void streamToTv(String uri) {
        turnOnOpenhabItem(tvStream, uri);
    }

    private void turnOnOpenhabItem(final String item, final String body) {
        try {
            String response =
                    Jsoup.connect(openhabApiUri + item)
                    .timeout(5000)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .header("Content-Type", "text/plain")
                    .header("Accept", "application/json")
                    .requestBody(body)
                    .execute()
                    .body();
            handleResponse(response);
        } catch (IOException e) {
            log.error("Error turning tv on:", e);
        }
    }

    private void handleResponse(String response) {
        if (response.isBlank()) {
            log.info("openhab: ok");
        } else {
            log.error("openhab: not ok: {}", response);
        }
    }

}
