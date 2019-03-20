package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.postnl.PostNLService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
@Slf4j
public class PostNLReportBusinessService extends AbstractBusinessService {

    private PostNLService postNLService;

    @Inject
    public PostNLReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService, PostNLService postNLService) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
        this.postNLService = postNLService;
    }

    public void report() {
        postNLService.getTodaysDeliveries();
    }
}
