package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.postnl.PostNLService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostNLReportBusinessService extends AbstractReportBusinessService {

    private PostNLService postNLService;

    @Autowired
    public PostNLReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput, PostNLService postNLService) {
        super(settingsService, reportedBusinessService, telegramOutput, googleHomeOutput);
        this.postNLService = postNLService;
    }

    public void report() {
        postNLService.getTodaysDeliveries();
    }
}
