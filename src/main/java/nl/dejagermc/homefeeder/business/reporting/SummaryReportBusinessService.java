package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
@Slf4j
public class SummaryReportBusinessService extends AbstractBusinessService {

    private DownloadReportBusinessService downloadReportBusinessService;
    private DotaReportBusinessService dotaReportBusinessService;
    private GarbageCollectionBusinessService garbageCollectionBusinessService;

    @Inject
    public SummaryReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService, DownloadReportBusinessService downloadReportBusinessService, DotaReportBusinessService dotaReportBusinessService, GarbageCollectionBusinessService garbageCollectionBusinessService) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
        this.downloadReportBusinessService = downloadReportBusinessService;
        this.dotaReportBusinessService = dotaReportBusinessService;
        this.garbageCollectionBusinessService = garbageCollectionBusinessService;
    }

    public void reportSummary() {
        if (settingsService.isHomeMuted()) {
            log.info("UC004: Home muted, saving summary voice report.");
            return;
        }

        downloadReportBusinessService.reportSummary();
        dotaReportBusinessService.reportSummary();
        garbageCollectionBusinessService.reportSummary();
    }
}
