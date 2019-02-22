package nl.dejagermc.homefeeder.business.reporting;

import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.business.reported.ReportedService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.user.UserState;

public class AbstractReportService {
    UserState userState;
    TelegramOutput telegramOutput;
    GoogleHomeOutput googleHomeOutput;
    ReportedService reportedService;

    public AbstractReportService(UserState userState, ReportedService reportedService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput) {
        this.userState = userState;
        this.reportedService = reportedService;
        this.telegramOutput = telegramOutput;
        this.googleHomeOutput = googleHomeOutput;
    }
}
