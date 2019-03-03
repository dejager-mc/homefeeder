package nl.dejagermc.homefeeder.business.reporting;

import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;

public class AbstractReportBusinessService {
    SettingsService settingsService;
    TelegramOutput telegramOutput;
    GoogleHomeOutput googleHomeOutput;
    ReportedBusinessService reportedBusinessService;

    public AbstractReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput) {
        this.settingsService = settingsService;
        this.reportedBusinessService = reportedBusinessService;
        this.telegramOutput = telegramOutput;
        this.googleHomeOutput = googleHomeOutput;
    }
}
