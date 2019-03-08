package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SummaryReportBusinessService extends AbstractReportBusinessService {

    private DownloadReportBusinessService downloadReportBusinessService;
    private DotaReportBusinessService dotaReportBusinessService;

    @Autowired
    public SummaryReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput, DownloadReportBusinessService downloadReportBusinessService, DotaReportBusinessService dotaReportBusinessService) {
        super(settingsService, reportedBusinessService, telegramOutput, googleHomeOutput);
        this.downloadReportBusinessService = downloadReportBusinessService;
        this.dotaReportBusinessService = dotaReportBusinessService;
    }

    public void reportSummaryToGoogleHome() {
        if (settingsService.surpressMessage()) {
            return;
        }

        downloadReportBusinessService.reportSummary();
        dotaReportBusinessService.reportSummary();
    }
}
