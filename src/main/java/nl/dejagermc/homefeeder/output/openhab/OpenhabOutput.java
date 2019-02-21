package nl.dejagermc.homefeeder.output.openhab;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
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

    private JsoupUtil jsoupUtil;

    @Autowired
    public OpenhabOutput(JsoupUtil jsoupUtil) {
        this.jsoupUtil = jsoupUtil;
    }

    public void turnOnTv() {
        turnOnOpenhabItem(tvSwitch, ON);
    }

    public void streamToTv(String uri) {
        turnOnOpenhabItem(tvStream, uri);
    }

    private void turnOnOpenhabItem(final String item, final String body) {
        String response = jsoupUtil.postToOpenhab(openhabApiUri + item, body);
        handleResponse(response);
    }

    private void handleResponse(String response) {
        if (response.isBlank()) {
            log.info("openhab: ok");
        } else {
            log.error("openhab: not ok: {}", response);
        }
    }

}
