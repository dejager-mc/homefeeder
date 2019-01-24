package nl.dejagermc.homefeeder.gathering.liquipedia.dota.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Match {
    private String leftTeam;
    private String rightTeam;
    private String gameType;
    private LocalDateTime matchTime;
    private String eventName;
    private String twitchChannel;
    private String youtubeChannel;

    public Match() {
    }

    @Override
    public String toString() {
        return "Match{" +
                "leftTeam='" + leftTeam + '\'' +
                ", rightTeam='" + rightTeam + '\'' +
                ", gameType='" + gameType + '\'' +
                ", matchTime=" + matchTime +
                ", eventName='" + eventName + '\'' +
                ", twitchChannel='" + twitchChannel + '\'' +
                ", youtubeChannel='" + youtubeChannel + '\'' +
                ", isLive=" + isLive() +
                '}';
    }

    public boolean isLive() {
        return getMatchTime().isBefore(LocalDateTime.now()) ? true : false;
    }

    public boolean matchEitherTeam(String team) {
        if (getLeftTeam().equals(team) || getRightTeam().equals(team)) {
            return true;
        }
        return false;
    }

    public String getYoutubeChannel() {
        return youtubeChannel;
    }

    public void setYoutubeChannel(String youtubeChannel) {
        this.youtubeChannel = youtubeChannel;
    }

    public String getTwitchChannel() {
        return twitchChannel;
    }

    public void setTwitchChannel(String twitchChannel) {
        this.twitchChannel = twitchChannel;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLeftTeam() {
        return leftTeam;
    }

    public void setLeftTeam(String leftTeam) {
        this.leftTeam = leftTeam;
    }

    public String getRightTeam() {
        return rightTeam;
    }

    public void setRightTeam(String rightTeam) {
        this.rightTeam = rightTeam;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public LocalDateTime getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(LocalDateTime matchTime) {
        this.matchTime = matchTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match match = (Match) o;
        return  (
                    (Objects.equals(leftTeam, match.leftTeam) || Objects.equals(leftTeam, match.rightTeam)) &&
                    (Objects.equals(rightTeam, match.rightTeam) || Objects.equals(rightTeam, match.leftTeam))
                ) &&
                Objects.equals(gameType, match.gameType) &&
                Objects.equals(eventName, match.eventName) &&
                Objects.equals(twitchChannel, match.twitchChannel) &&
                Objects.equals(youtubeChannel, match.youtubeChannel) &&
                Objects.equals(matchTime, match.matchTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftTeam, rightTeam, gameType, matchTime, eventName, twitchChannel, youtubeChannel);
    }
}
