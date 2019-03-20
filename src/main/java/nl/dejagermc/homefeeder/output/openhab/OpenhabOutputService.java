package nl.dejagermc.homefeeder.output.openhab;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.openhab.OpenhabInputService;
import nl.dejagermc.homefeeder.input.openhab.model.OpenhabItem;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class OpenhabOutputService {

    private static final String ON = "ON";

    @Value("${openhab.homefeeder.online}")
    private String homefeederIsOnlineItemName;

    private HttpUtil httpUtil;
    private OpenhabInputService openhabInputService;

    @Autowired
    public OpenhabOutputService(HttpUtil httpUtil, OpenhabInputService openhabInputService) {
        this.httpUtil = httpUtil;
        this.openhabInputService = openhabInputService;
    }

    public boolean performActionOnSwitchItem(String action, OpenhabItem item) {
        String response = httpUtil.postJsonToOpenhab(item.getLink(), action);
        return isCorrectResponse(response);
    }

    public boolean performActionOnStringItem(String action, OpenhabItem item) {
        String response = httpUtil.postJsonToOpenhab(item.getLink(), action);
        return isCorrectResponse(response);
    }

    public boolean homefeederIsOnline() {
        Optional<OpenhabItem> item = openhabInputService.findOpenhabItemWithName(homefeederIsOnlineItemName);
        if (item.isPresent()) {
            return performActionOnSwitchItem(ON, item.get());
        }
        return false;
    }

    private boolean isCorrectResponse(String response) {
        if (response.isBlank()) {
            return true;
        } else {
            log.error("UC501: Error sending command to openhab: {}", response);
            return false;
        }
    }

}
