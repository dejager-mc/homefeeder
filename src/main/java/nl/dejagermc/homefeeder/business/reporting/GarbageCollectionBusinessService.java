package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.groningen.garbishcollection.GarbageCollectionService;
import nl.dejagermc.homefeeder.input.groningen.garbishcollection.model.BinPickup;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkState;
import static nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod.GOOGLE_HOME;
import static nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod.TELEGRAM;

@Service
@Slf4j
public class GarbageCollectionBusinessService extends AbstractBusinessService {

    private static final String TELEGRAM_MESSAGE = "<b>%s</b>%nTomorrow %s.";
    private static final String GOOGLE_HOME_MESSAGE = "%s bin, out tomorrow.";

    private GarbageCollectionService garbageCollectionService;

    @Inject
    public GarbageCollectionBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService, GarbageCollectionService garbageCollectionService) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
        this.garbageCollectionService = garbageCollectionService;
    }

    public void reportNextBinPickup() {
        garbageCollectionService.getNextBinPickup().ifPresentOrElse(this::reportTomorrowBinPickup, () -> log.info("UC001: reporting: No bin pickup found"));
    }

    private void reportTomorrowBinPickup(BinPickup binPickup) {
        // if the next pickup day is tomorrow
        if (binPickup.getPickupDay().isEqual(LocalDate.now().plusDays(1))) {
            log.info("UC001: Reporting bin pickup scheduled for tomorrow: {}", binPickup);
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
        }
    }

    private void reportToGoogleHome(BinPickup binPickup) {
        if (!reportedBusinessService.hasThisBeenReportedToThat(binPickup, GOOGLE_HOME)) {
            if (settingsService.isHomeMuted()) {
                garbageCollectionService.addNotReported(binPickup);
            } else {
                String message = String.format(GOOGLE_HOME_MESSAGE, binPickup.getBinType());
                googleHomeOutputService.broadcast(message);

                reportedBusinessService.markThisReportedToThat(binPickup, GOOGLE_HOME);
            }
        }
    }

    void reportSummary() {
        log.info("UC004: reporting not reported bin pickups");
        List<BinPickup> notReported = garbageCollectionService.getNotReported();
        if (!notReported.isEmpty()) {
            checkState(notReported.size()==1, "Expected only 1 saved bin pickup.");
            BinPickup binPickup = notReported.get(0);
            String message = String.format(GOOGLE_HOME_MESSAGE, binPickup.getBinType());
            googleHomeOutputService.broadcast(message);
            reportedBusinessService.markThisReportedToThat(binPickup, GOOGLE_HOME);
            garbageCollectionService.resetNotReported();
        }


    }
}
