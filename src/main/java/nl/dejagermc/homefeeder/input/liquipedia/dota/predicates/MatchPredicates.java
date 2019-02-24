package nl.dejagermc.homefeeder.input.liquipedia.dota.predicates;

import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

public class MatchPredicates {

    private MatchPredicates() {
        // private
    }

    public static Predicate<Match> isMatchThatWillTakePlaceLaterToday() {
        return match -> match.matchTime().isAfter(LocalDateTime.now()) &&
                match.matchTime().isBefore(LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
    }

    public static Predicate<Match> isMatchThatHappensToday() {
        return match -> match.matchTime().isAfter(LocalDateTime.now().toLocalDate().atStartOfDay()) &&
                match.matchTime().isBefore(LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
    }

    public static Predicate<Match> isMatchWithStream() {
        return match -> !match.twitchChannel().isBlank() || !match.youtubeChannel().isBlank();
    }

    public static Predicate<Match> isMatchWithOneOfTheseTeams(List<String> teams) {
        return match -> match.matchEitherTeam(teams);
    }

}
