package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reported.ReportedService;
import nl.dejagermc.homefeeder.input.postnl.PostNLService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostNLReportService extends AbstractReportService {

    private PostNLService postNLService;

    @Autowired
    public PostNLReportService(UserState userState, ReportedService reportedService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput, PostNLService postNLService) {
        super(userState, reportedService, telegramOutput, googleHomeOutput);
        this.postNLService = postNLService;
    }

    public void report() {
        postNLService.getTodaysDeliveries();
    }
}
