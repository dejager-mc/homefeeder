package nl.dejagermc.homefeeder.business.reporting;

import nl.dejagermc.homefeeder.business.reported.ReportedService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;

public class AbstractReportService {
    SettingsService settingsService;
    TelegramOutput telegramOutput;
    GoogleHomeOutput googleHomeOutput;
    ReportedService reportedService;

    public AbstractReportService(SettingsService settingsService, ReportedService reportedService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput) {
        this.settingsService = settingsService;
        this.reportedService = reportedService;
        this.telegramOutput = telegramOutput;
        this.googleHomeOutput = googleHomeOutput;
    }
}
