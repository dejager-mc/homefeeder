package nl.dejagermc.homefeeder.input.homefeeder.dto;

import lombok.experimental.Accessors;

import java.util.List;

@Accessors
public class HomeFeederSettingsDto {
    // openhab
    private boolean isHome;
    private boolean isSleeping;
    private boolean isMute;

    // settings
    private boolean useGoogleHome;
    private boolean useTelegram;
    private List<String> favoriteTeams;

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
