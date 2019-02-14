package nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Predicate;

public class MatchPredicates {

    private MatchPredicates() {
        // private
    }

    public static Predicate<Match> isEenMatchDieLaterVandaagIs() {
        return match -> match.matchTime().isAfter(LocalDateTime.now()) &&
                match.matchTime().isBefore(LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
    }

    public static Predicate<Match> isEenMatchDieVandaagIs() {
        return match -> match.matchTime().isAfter(LocalDateTime.now().toLocalDate().atStartOfDay()) &&
                match.matchTime().isBefore(LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
    }

    public static Predicate<Match> isMatchMetStream() {
        return match -> !match.twitchChannel().isBlank() || !match.youtubeChannel().isBlank();
    }

    public static Comparator<Match> sortMatchesOpTijd() {
        return Comparator
                .comparing(Match::matchTime)
                .thenComparing(Match::tournamentName)
                .thenComparing(Match::leftTeam)
                .thenComparing(Match::rightTeam);
    }
}
