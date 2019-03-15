package nl.dejagermc.homefeeder.business.tradfri;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.openhab.OpenhabInputService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.output.tradfri.TradfriException;
import nl.dejagermc.homefeeder.output.tradfri.TradfriOutput;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TradfriBusinessService extends AbstractBusinessService {

    private static final String TELEGRAM_GATEWAY_REBOOT_START = "Tradfri reboot initiated.";
    private static final String TELEGRAM_GATEWAY_REBOOT_START_FAILED = "Tradfri reboot could not be initiated.";
    private static final String TELEGRAM_GATEWAY_REBOOT_UP = "Tradfri reboot successful, gateway is up.";
    private static final String TELEGRAM_GATEWAY_REBOOT_DOWN = "Tradfri reboot failed, gateway is not up.";

    private static final String GOOGLE_HOME_GATEWAY_UP = "The ikea gateway is up.";
    private static final String GOOGLE_HOME_GATEWAY_DOWN = "The ikea gateway is down.";

    private static final String OPENHAB_ITEM_EVENT_GATEWAY_HAS_REBOOTED = "Tradfri_Gateway_Rebooted";

    private TradfriOutput tradfriOutput;
    private OpenhabInputService openhabInputService;
    private JsoupUtil jsoupUtil;

    @Autowired
    public TradfriBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput, TradfriOutput tradfriOutput, OpenhabInputService openhabInputService, JsoupUtil jsoupUtil) {
        super(settingsService, reportedBusinessService, telegramOutput, googleHomeOutput);
        this.tradfriOutput = tradfriOutput;
        this.openhabInputService = openhabInputService;
        this.jsoupUtil = jsoupUtil;
    }

    public void rebootGateway() {
        log.info("UC200: reboot tradfri gateway");
        boolean gatewayIsUp = false;
        telegramOutput.sendMessage(TELEGRAM_GATEWAY_REBOOT_START);
        boolean rebootStarted = tradfriOutput.rebootGateway();

        if (!rebootStarted) {
            telegramOutput.sendMessage(TELEGRAM_GATEWAY_REBOOT_START_FAILED);
            return;
        }

        try {
            TimeUnit.SECONDS.sleep(2);
            gatewayIsUp = isGatewayUp();
        } catch (InterruptedException e) {
            log.error("UC200: Reboot: error while waiting");
            Thread.currentThread().interrupt();
        }

        if (gatewayIsUp) {
            log.info("UC200: gateway reboot successful.");
            telegramOutput.sendMessage(TELEGRAM_GATEWAY_REBOOT_UP);
            sendEventToOpenhabGatewayIsUp();
        } else {
            log.info("UC200: gateway reboot failed, gateway is down.");
            telegramOutput.sendMessage(TELEGRAM_GATEWAY_REBOOT_DOWN);
        }
    }

    private void sendEventToOpenhabGatewayIsUp() {
        openhabInputService.findOpenhabThing(OPENHAB_ITEM_EVENT_GATEWAY_HAS_REBOOTED)
                .ifPresentOrElse(
                        item -> jsoupUtil.postJsonToOpenhab(item.getLink(), "ON"),
                        () -> log.error("UC200: can not inform openhab gateway is up")
                );
    }

    public void reportGatewayStatusToGoogleHome() {
        log.info("UC202: reporting gateway status to google home");
        boolean isGatewayUp = isGatewayUp();
        if (settingsService.userIsAvailable()) {
            log.info("UC202: reporting to google home");
            if (isGatewayUp) {
                googleHomeOutput.broadcast(GOOGLE_HOME_GATEWAY_UP);
            } else {
                googleHomeOutput.broadcast(GOOGLE_HOME_GATEWAY_DOWN);
            }
        }
    }

    private boolean isGatewayUp() {
        log.info("UC202: checking gateway status");
        boolean isUp = false;
        try {
            isUp = tradfriOutput.isGatewayUpRetryable();
            log.info("UC202: gateway is up");
        } catch (TradfriException t) {
            log.error("UC202: gateway is down");
        }
        return isUp;
    }

    public String getAllDevices() {
        log.info("UC201: reporting all devies");
        return tradfriOutput.getAllDevices();
    }
}
