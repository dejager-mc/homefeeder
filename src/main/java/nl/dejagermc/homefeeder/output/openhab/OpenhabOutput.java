package nl.dejagermc.homefeeder.output.openhab;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.enums.StreamTarget;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OpenhabOutput {

    private static final String ON = "ON";

    @Value("${openhab.rest}")
    private String openhabApiUri;

    // openhab switch items
    @Value("${openhab.tv.switch}")
    private String sonyTvLivingRoomSwitch;
    @Value("${openhab.tv.lg.switch}")
    private String lgTvCinemaSwitch;
    @Value("${openhab.pc.asgard.switch}")
    private String asgardPcLivingRoomSwitch;

    // openhab stream items
    @Value("${openhab.tv.stream}")
    private String sonyTvLivingRoom;
    @Value("${openhab.tv.lg.stream}")
    private String lgTvCinema;
    @Value("${openhab.pc.asgard.stream}")
    private String asgardPcLivingRoom;

    @Value("${openhab.homefeeder.online}")
    private String homefeederIsOnline;

    private JsoupUtil jsoupUtil;

    @Autowired
    public OpenhabOutput(JsoupUtil jsoupUtil) {
        this.jsoupUtil = jsoupUtil;
    }

    public boolean turnOnTv() {
        return turnOnOpenhabItem(sonyTvLivingRoomSwitch, ON);
    }

    public boolean streamToTv(String uri) {
        return turnOnOpenhabItem(sonyTvLivingRoom, uri);
    }

    public boolean streamToDevice(String uri, StreamTarget streamTarget) {
        switch (streamTarget) {
            case SONY_TV_LIVING_ROOM:
                return turnOnOpenhabItem(sonyTvLivingRoom, uri);
            case LG_TV_CINEMA:
                return turnOnOpenhabItem(lgTvCinema, uri);
            case ASGARD_PC_LIVING_ROOM:
                return turnOnOpenhabItem(asgardPcLivingRoom, uri);
            default:
                return false;
        }
    }

    public boolean homefeederIsOnline() {
        return turnOnOpenhabItem(homefeederIsOnline, ON);
    }

    private boolean turnOnOpenhabItem(final String item, final String body) {
        String response = jsoupUtil.postJson(openhabApiUri + item, body);
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
