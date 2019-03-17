package nl.dejagermc.homefeeder.business;

import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;

public abstract class AbstractBusinessService {
    public SettingsService settingsService;
    public TelegramOutputService telegramOutputService;
    public GoogleHomeOutputService googleHomeOutputService;
    public ReportedBusinessService reportedBusinessService;

    public AbstractBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService) {
        this.settingsService = settingsService;
        this.reportedBusinessService = reportedBusinessService;
        this.telegramOutputService = telegramOutputService;
        this.googleHomeOutputService = googleHomeOutputService;
    }
}
