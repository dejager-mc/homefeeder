package nl.dejagermc.homefeeder.input.liquipedia.dota.builders;

import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;

import java.time.LocalDateTime;

public class MatchBuilders {

    public static Match defaultMatch(String leftTeam, String rightTeam, String tournamentName, boolean isLive) {
        LocalDateTime time;
        if (isLive) {
            time = LocalDateTime.now();
        } else {
            time = LocalDateTime.now().plusHours(1);
        }

        return Match.builder()
                .matchTime(time)
                .gameType("Bo3")
                .leftTeam(leftTeam)
                .rightTeam(rightTeam)
                .tournamentName(tournamentName)
                .twitchChannel(tournamentName)
                .youtubeChannel("")
                .build();
    }

    public static Match tomorrowMatch(String leftTeam, String rightTeam, String tournamentName) {
        LocalDateTime time = LocalDateTime.now().plusDays(1);

        return Match.builder()
                .matchTime(time)
                .gameType("Bo3")
                .leftTeam(leftTeam)
                .rightTeam(rightTeam)
                .tournamentName(tournamentName)
                .twitchChannel(tournamentName)
                .youtubeChannel("")
                .build();
    }

    public static Match notActiveTournamentMatch(String leftTeam, String rightTeam, String tournamentName) {
        LocalDateTime time = LocalDateTime.now().plusMonths(2);

        return Match.builder()
                .matchTime(time)
                .gameType("Bo3")
                .leftTeam(leftTeam)
                .rightTeam(rightTeam)
                .tournamentName(tournamentName)
                .twitchChannel(tournamentName)
                .youtubeChannel("")
                .build();
    }
}
