package nl.dejagermc.homefeeder.business;

import nl.dejagermc.homefeeder.reporting.google.home.HomeBroadcaster;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private HomeBroadcaster homeBroadcaster;
    private UserState userState;
    private ReportUtil reportUtil;

    @Autowired
    public ReportService(HomeBroadcaster homeBroadcaster, UserState userState, ReportUtil reportUtil) {
        this.homeBroadcaster = homeBroadcaster;
        this.userState = userState;
        this.reportUtil = reportUtil;
    }

    public void reportWhenArrivingAtHome() {
        // films gedownload?
        // series gedownload?
        // actieve dota tournaments die zijn begonnen?
        // actieve dota tournaments die vandaag eindigen
        // dota teams die ik volg hebben vandaag al gespeeld
        // dota teams die ik volg spelen vandaag nog
        StringBuilder sb = new StringBuilder();
        reportUtil.addDownloadedMoviesToReport(sb);

        homeBroadcaster.broadcastMessage(sb.toString());
    }

    public void reportWhenWakingUp() {

    }

    public void reportImportantDotaTeamPlayingNow() {

    }
}
