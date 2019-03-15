package nl.dejagermc.homefeeder.business;

import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;

public abstract class AbstractBusinessService {
    public SettingsService settingsService;
    public TelegramOutput telegramOutput;
    public GoogleHomeOutput googleHomeOutput;
    public ReportedBusinessService reportedBusinessService;

    public AbstractBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput) {
        this.settingsService = settingsService;
        this.reportedBusinessService = reportedBusinessService;
        this.telegramOutput = telegramOutput;
        this.googleHomeOutput = googleHomeOutput;
    }
}
