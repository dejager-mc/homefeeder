package nl.dejagermc.homefeeder.gathering.liquipedia.dota.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
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
        return matchTime().isBefore(LocalDateTime.now()) ? true : false;
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
                Objects.equals(tournamentName, match.tournamentName) &&
                Objects.equals(twitchChannel, match.twitchChannel) &&
                Objects.equals(youtubeChannel, match.youtubeChannel) &&
                Objects.equals(matchTime, match.matchTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftTeam, rightTeam, gameType, matchTime, tournamentName, twitchChannel, youtubeChannel);
    }
}
