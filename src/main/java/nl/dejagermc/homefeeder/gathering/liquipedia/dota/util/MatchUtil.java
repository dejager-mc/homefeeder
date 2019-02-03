package nl.dejagermc.homefeeder.gathering.liquipedia.dota.util;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MatchUtil {

    public static List<Match> getLiveMatches(List<Match> matches) {
        return matches.stream().filter(Match::isLive).collect(Collectors.toList());
    }

    public static Optional<Match> getNextMatchForTeam(List<Match> matches, String team) {
        return matches.stream().filter(m -> m.matchEitherTeam(team)).findFirst();
    }
}
