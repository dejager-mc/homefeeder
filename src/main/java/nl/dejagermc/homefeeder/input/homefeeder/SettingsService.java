package nl.dejagermc.homefeeder.input.homefeeder;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods;
import nl.dejagermc.homefeeder.input.homefeeder.model.DotaSettings;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederSettings;
import nl.dejagermc.homefeeder.input.homefeeder.model.OpenHabSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SettingsService {
    private OpenHabSettings openHabSettings;
    private HomeFeederSettings homeFeederSettings;
    private DotaSettings dotaSettings;

    @Autowired
    public SettingsService(OpenHabSettings openHabSettings, HomeFeederSettings homeFeederSettings, DotaSettings dotaSettings) {
        this.openHabSettings = openHabSettings;
        this.homeFeederSettings = homeFeederSettings;
        this.dotaSettings = dotaSettings;
    }

    public boolean isUserAbleToGetReport() {
        return openHabSettings.isHome() && !openHabSettings.isMute() && !openHabSettings.isSleeping();
    }

    public List<String> getFavoriteDotaTeams() {
        return dotaSettings.getFavoriteTeams();
    }

    public List<ReportMethods> getReportMethods() {
        List<ReportMethods> reportMethods = Collections.emptyList();
        if (homeFeederSettings.isUseTelegram()) {
            reportMethods.add(ReportMethods.TELEGRAM);
        }
        if (homeFeederSettings.isUseGoogleHome()) {
            reportMethods.add(ReportMethods.GOOGLE_HOME);
        }
        return reportMethods;
    }

    public OpenHabSettings getOpenHabSettings() {
        return openHabSettings;
    }

    public HomeFeederSettings getHomeFeederSettings() {
        return homeFeederSettings;
    }

    public DotaSettings getDotaSettings() {
        return dotaSettings;
    }
}
