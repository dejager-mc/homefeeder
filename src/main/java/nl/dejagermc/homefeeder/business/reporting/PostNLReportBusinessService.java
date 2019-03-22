package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.postnl.PostNLService;
import nl.dejagermc.homefeeder.input.postnl.model.Delivery;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod.GOOGLE_HOME;
import static nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod.TELEGRAM;

@Service
@Slf4j
public class PostNLReportBusinessService extends AbstractBusinessService {

    private static final String TELEGRAM_MESSAGE = "<b>New delivery:</b>%nSender: %s%nExpected delivery date: %s";
    private static final String GOOGLE_HOME_MESSAGE = "";


    private PostNLService postNLService;

    @Inject
    public PostNLReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService, PostNLService postNLService) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
        this.postNLService = postNLService;
    }

    public void report() {
        postNLService.getTodaysDeliveries();
    }

    public void reportSummary() {
        List<Delivery> savedDeliveries = postNLService.getAllSavedDeliveries();
    }

    private void reportToTelegram(Delivery delivery) {
        String message = String.format(TELEGRAM_MESSAGE, delivery.sender(), delivery.getFormattedDeliveryTime());
        telegramOutputService.sendMessage(message);
        reportedBusinessService.markThisReportedToThat(delivery, TELEGRAM);
    }

    private void reportToGoogleHome(Delivery delivery) {
        if (settingsService.isHomeMuted()) {
            postNLService.addSavedDelivery(delivery);
        } else {


            reportedBusinessService.markThisReportedToThat(delivery, GOOGLE_HOME);
        }
    }
}
