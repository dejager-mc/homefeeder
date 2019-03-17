package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SummaryReportBusinessService extends AbstractBusinessService {

    private DownloadReportBusinessService downloadReportBusinessService;
    private DotaReportBusinessService dotaReportBusinessService;

    @Autowired
    public SummaryReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService, DownloadReportBusinessService downloadReportBusinessService, DotaReportBusinessService dotaReportBusinessService) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
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
