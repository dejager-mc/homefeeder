package nl.dejagermc.homefeeder.input.liquipedia.dota.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Accessors(fluent = true)
@Getter
@Builder
@ToString
public class Match {
    private String leftTeam;
    private String rightTeam;
    private String gameType;
    private LocalDateTime matchTime;
    private String tournamentName;
    private String twitchChannel;
    private String youtubeChannel;

    public boolean isLive() {
        return !matchTime().isAfter(LocalDateTime.now());
    }

    public boolean isInTournament(String tournamentName) {
        return this.tournamentName.equals(tournamentName);
    }

    public boolean matchEitherTeam(List<String> teams) {
        for (String team : teams) {
            if (matchEitherTeam(team)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchEitherTeam(String team) {
        return (leftTeam().equals(team) || rightTeam().equals(team));
    }

    public String getDateTimeFormattedTimeToday() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm", Locale.US);
        return matchTime.format(formatter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match match = (Match) o;
        return  Objects.equals(leftTeam, match.leftTeam) &&
                Objects.equals(rightTeam, match.rightTeam) &&
                Objects.equals(gameType, match.gameType) &&
                Objects.equals(tournamentName, match.tournamentName) &&
                Objects.equals(twitchChannel, match.twitchChannel) &&
                Objects.equals(youtubeChannel, match.youtubeChannel) &&
                Objects.equals(isLive(), match.isLive());
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftTeam, rightTeam, gameType, tournamentName, twitchChannel, youtubeChannel);
    }
}
