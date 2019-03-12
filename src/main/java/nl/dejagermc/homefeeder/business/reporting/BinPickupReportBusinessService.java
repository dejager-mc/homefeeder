package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.groningen.rubbish.RubbishService;
import nl.dejagermc.homefeeder.input.groningen.rubbish.model.BinPickup;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import static nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods.GOOGLE_HOME;
import static nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods.TELEGRAM;

@Service
@Slf4j
public class BinPickupReportBusinessService extends AbstractReportBusinessService {

    private static final String TELEGRAM_MESSAGE = "<b>%s</b>%nTomorrow %s.";
    private static final String GOOGLE_HOME_MESSAGE = "%s bin, out tomorrow.";

    private RubbishService rubbishService;

    @Autowired
    public BinPickupReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput, RubbishService rubbishService) {
        super(settingsService, reportedBusinessService, telegramOutput, googleHomeOutput);
        this.rubbishService = rubbishService;
    }

    public void reportNextBinPickup() {
        Optional<BinPickup> binPickupOptional = rubbishService.getNextBinPickup();
        if (binPickupOptional.isPresent()) {
            BinPickup binPickup = binPickupOptional.get();
            // if the next pickup day is tomorrow
            if (binPickup.getPickupDay().isEqual(LocalDate.now().plusDays(1))) {
                reportToTelegram(binPickupOptional.get());
                reportToGoogleHome(binPickupOptional.get());
            }
        }
    }

    private void reportToTelegram(BinPickup binPickup) {
        if (!reportedBusinessService.hasThisBeenReportedToThat(binPickup, TELEGRAM)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.US);
            String date = binPickup.getPickupDay().format(formatter);
            String message = String.format(TELEGRAM_MESSAGE, binPickup.getBinType().getName(), date);
            telegramOutput.sendMessage(message);

            reportedBusinessService.markThisReportedToThat(binPickup, TELEGRAM);
        }
    }

    private void reportToGoogleHome(BinPickup binPickup) {
        if (!reportedBusinessService.hasThisBeenReportedToThat(binPickup, GOOGLE_HOME)) {
            if (!settingsService.surpressMessage()) {
                String message = String.format(GOOGLE_HOME_MESSAGE, binPickup.getBinType());
                googleHomeOutput.broadcast(message);

                reportedBusinessService.markThisReportedToThat(binPickup, GOOGLE_HOME);
            }
        }
    }
}
