package nl.dejagermc.homefeeder.business.tradfri;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.openhab.OpenhabInputService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import nl.dejagermc.homefeeder.output.tradfri.TradfriException;
import nl.dejagermc.homefeeder.output.tradfri.TradfriService;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
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

    private TradfriService tradfriService;
    private OpenhabInputService openhabInputService;
    private HttpUtil httpUtil;

    @Inject
    public TradfriBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService, TradfriService tradfriService, OpenhabInputService openhabInputService, HttpUtil httpUtil) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
        this.tradfriService = tradfriService;
        this.openhabInputService = openhabInputService;
        this.httpUtil = httpUtil;
    }

    public void rebootGateway() {
        log.info("UC200: reboot tradfri gateway.");
        boolean gatewayIsUp = false;
        telegramOutputService.sendMessage(TELEGRAM_GATEWAY_REBOOT_START);
        boolean rebootStarted = tradfriService.rebootGateway();

        if (!rebootStarted) {
            telegramOutputService.sendMessage(TELEGRAM_GATEWAY_REBOOT_START_FAILED);
            return;
        }

        try {
            TimeUnit.SECONDS.sleep(2);
            gatewayIsUp = isGatewayUp();
        } catch (InterruptedException e) {
            log.error("UC200: error while waiting for reboot.");
            Thread.currentThread().interrupt();
        }

        if (gatewayIsUp) {
            log.info("UC200: gateway reboot successful.");
            telegramOutputService.sendMessage(TELEGRAM_GATEWAY_REBOOT_UP);
            sendEventToOpenhabGatewayIsUp();
        } else {
            log.info("UC200: gateway reboot failed, gateway is down.");
            telegramOutputService.sendMessage(TELEGRAM_GATEWAY_REBOOT_DOWN);
        }
    }

    private void sendEventToOpenhabGatewayIsUp() {
        openhabInputService.findOpenhabItemWithLabel(OPENHAB_ITEM_EVENT_GATEWAY_HAS_REBOOTED)
                .ifPresentOrElse(
                        item -> httpUtil.postJsonToOpenhab(item.getLink(), "ON"),
                        () -> log.error("UC200: can not inform openhab gateway is up.")
                );
    }

    public void reportGatewayStatus() {
        boolean isGatewayUp = isGatewayUp();

        if (isGatewayUp) {
            telegramOutputService.sendMessage(TELEGRAM_GATEWAY_REBOOT_UP);
        } else {
            telegramOutputService.sendMessage(TELEGRAM_GATEWAY_REBOOT_DOWN);
        }

        if (!settingsService.isHomeMuted()) {
            if (isGatewayUp) {
                googleHomeOutputService.broadcast(GOOGLE_HOME_GATEWAY_UP);
            } else {
                googleHomeOutputService.broadcast(GOOGLE_HOME_GATEWAY_DOWN);
            }
        }
    }

    private boolean isGatewayUp() {
        log.info("UC202: checking gateway status.");
        boolean isUp = false;
        try {
            isUp = tradfriService.isGatewayUpRetryable();
            log.info("UC202: gateway is up.");
        } catch (TradfriException t) {
            log.error("UC202: gateway is down.");
        }
        return isUp;
    }

    public String getAllDevices() {
        log.info("UC201: reporting all deviecs");
        return tradfriService.getAllDevices();
    }
}
