package nl.dejagermc.homefeeder.business;

import nl.dejagermc.homefeeder.output.google.home.GoogleHomeReporter;
import nl.dejagermc.homefeeder.output.reported.ReportedService;
import nl.dejagermc.homefeeder.output.telegram.TelegramReporter;
import nl.dejagermc.homefeeder.user.UserState;

public class AbstractReportService {
    UserState userState;
    TelegramReporter telegramReporter;
    GoogleHomeReporter googleHomeReporter;
    ReportedService reportedService;

    public AbstractReportService(UserState userState, ReportedService reportedService, TelegramReporter telegramReporter, GoogleHomeReporter googleHomeReporter) {
        this.userState = userState;
        this.reportedService = reportedService;
        this.telegramReporter = telegramReporter;
        this.googleHomeReporter = googleHomeReporter;
    }
}
