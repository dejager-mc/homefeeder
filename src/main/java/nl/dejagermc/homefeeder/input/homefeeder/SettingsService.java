package nl.dejagermc.homefeeder.input.homefeeder;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods;
import nl.dejagermc.homefeeder.input.homefeeder.model.DotaSettings;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederSettings;
import nl.dejagermc.homefeeder.input.homefeeder.model.OpenHabSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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

    public boolean userIsListening() {
        return openHabSettings.isListening();
    }

    public boolean surpressMessage() {
        return openHabSettings.isMute();
    }

    public List<String> getFavoriteDotaTeams() {
        return dotaSettings.getFavoriteTeams();
    }

    @Cacheable(cacheNames = "getReportMethods", cacheManager = "cacheManagerCaffeine")
    public Set<ReportMethods> getReportMethods() {
        return homeFeederSettings.getReportMethods();
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
