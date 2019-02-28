package nl.dejagermc.homefeeder.input.homefeeder.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Accessors(fluent = true)
@Getter
@Setter
@Component
@NoArgsConstructor
public class HomeFeederState {
    // openhab
    private boolean isHome = true;
    private boolean isSleeping = false;
    private boolean isMute = false;

    // settings
    @Value("${homefeeder.use.googlehome}")
    private boolean useGoogleHome;
    @Value("${homefeeder.use.telegram}")
    private boolean useTelegram;
    @Value("${homefeeder.dota.favorite.teams}")
    private List<String> favoriteTeams;

    public boolean reportNow() {
        return !isSleeping && !isMute && isHome;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    public void setSleeping(boolean sleeping) {
        isSleeping = sleeping;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public boolean isUseGoogleHome() {
        return useGoogleHome;
    }

    public void setUseGoogleHome(boolean useGoogleHome) {
        this.useGoogleHome = useGoogleHome;
    }

    public boolean isUseTelegram() {
        return useTelegram;
    }

    public void setUseTelegram(boolean useTelegram) {
        this.useTelegram = useTelegram;
    }

    public List<String> getFavoriteTeams() {
        return favoriteTeams;
    }

    public void setFavoriteTeams(List<String> favoriteTeams) {
        this.favoriteTeams = favoriteTeams;
    }
}
