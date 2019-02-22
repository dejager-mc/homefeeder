package nl.dejagermc.homefeeder.output.openhab;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Value("${openhab.homefeeder.online}")
    private String homefeederIsOnline;

    private JsoupUtil jsoupUtil;

    @Autowired
    public OpenhabOutput(JsoupUtil jsoupUtil) {
        this.jsoupUtil = jsoupUtil;
    }

    public boolean turnOnTv() {
        return turnOnOpenhabItem(tvSwitch, ON);
    }

    public boolean streamToTv(String uri) {
        return turnOnOpenhabItem(tvStream, uri);
    }

    public boolean homefeederIsOnline() {
        return turnOnOpenhabItem(homefeederIsOnline, ON);
    }

    private boolean turnOnOpenhabItem(final String item, final String body) {
        String response = jsoupUtil.postToOpenhab(openhabApiUri + item, body);
        return isCorrectResponse(response);
    }

    private boolean isCorrectResponse(String response) {
        if (response.isBlank()) {
            return true;
        } else {
            log.error("openhab response not ok: {}", response);
            return false;
        }
    }

}
