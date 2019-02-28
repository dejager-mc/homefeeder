package nl.dejagermc.homefeeder.business.reporting;

import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.business.reported.ReportedService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;

public class AbstractReportService {
    HomeFeederState homeFeederState;
    TelegramOutput telegramOutput;
    GoogleHomeOutput googleHomeOutput;
    ReportedService reportedService;

    public AbstractReportService(HomeFeederState homeFeederState, ReportedService reportedService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput) {
        this.homeFeederState = homeFeederState;
        this.reportedService = reportedService;
        this.telegramOutput = telegramOutput;
        this.googleHomeOutput = googleHomeOutput;
    }
}
