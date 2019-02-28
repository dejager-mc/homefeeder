package nl.dejagermc.homefeeder.input.homefeeder;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;
import nl.dejagermc.homefeeder.input.homefeeder.model.OpenHabSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SettingsService {
    private OpenHabSettings openHabSettings;
    private HomeFeederState homeFeederState;

    @Autowired
    public SettingsService(OpenHabSettings openHabSettings, HomeFeederState homeFeederState) {
        this.openHabSettings = openHabSettings;
        this.homeFeederState = homeFeederState;
    }

    public boolean isUserAbleToGetReport() {
        return openHabSettings.isHome() && !openHabSettings.isMute() && !openHabSettings.isSleeping();
    }

    public List<String> getFavoriteDotaTeams() {
        return homeFeederState.favoriteTeams();
    }

    public List<ReportMethods> getReportMethods() {
        List<ReportMethods> reportMethods = Collections.emptyList();
        if (homeFeederState.useTelegram()) {
            reportMethods.add(ReportMethods.TELEGRAM);
        }
        if (homeFeederState.useGoogleHome()) {
            reportMethods.add(ReportMethods.GOOGLE_HOME);
        }
        return reportMethods;
    }
}
