package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.groningen.rubbish.RubbishService;
import nl.dejagermc.homefeeder.input.groningen.rubbish.model.BinPickup;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods.GOOGLE_HOME;
import static nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods.TELEGRAM;

@Service
@Slf4j
public class BinPickupReportBusinessService extends AbstractBusinessService {

    private static final String TELEGRAM_MESSAGE = "<b>%s</b>%nTomorrow %s.";
    private static final String GOOGLE_HOME_MESSAGE = "%s bin, out tomorrow.";

    private RubbishService rubbishService;

    @Autowired
    public BinPickupReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService, RubbishService rubbishService) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
        this.rubbishService = rubbishService;
    }

    public void reportNextBinPickup() {
        rubbishService.getNextBinPickup().ifPresentOrElse(this::reportTomorrowBinPickup, () -> log.info("UC001: reporting: No bin pickup found"));
    }

    private void reportTomorrowBinPickup(BinPickup binPickup) {
        log.info("UC001: reporting: found bin pickup: {}", binPickup);
        // if the next pickup day is tomorrow
        if (binPickup.getPickupDay().isEqual(LocalDate.now().plusDays(1))) {
            reportToTelegram(binPickup);
            reportToGoogleHome(binPickup);
        }
    }

    private void reportToTelegram(BinPickup binPickup) {
        if (!reportedBusinessService.hasThisBeenReportedToThat(binPickup, TELEGRAM)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.US);
            String date = binPickup.getPickupDay().format(formatter);
            String message = String.format(TELEGRAM_MESSAGE, binPickup.getBinType().getName(), date);
            telegramOutputService.sendMessage(message);

            reportedBusinessService.markThisReportedToThat(binPickup, TELEGRAM);
            log.info("UC001: reported: reported to telegram");
        }
    }

    private void reportToGoogleHome(BinPickup binPickup) {
        if (!reportedBusinessService.hasThisBeenReportedToThat(binPickup, GOOGLE_HOME)) {
            if (settingsService.userIsAvailable()) {
                String message = String.format(GOOGLE_HOME_MESSAGE, binPickup.getBinType());
                googleHomeOutputService.broadcast(message);

                reportedBusinessService.markThisReportedToThat(binPickup, GOOGLE_HOME);
                log.info("UC001: reported: reported to google home");
            }
        }
    }
}
