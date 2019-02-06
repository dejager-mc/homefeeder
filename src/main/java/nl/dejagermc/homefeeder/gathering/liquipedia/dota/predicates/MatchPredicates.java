package nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class MatchPredicates {

    private MatchPredicates() {
        // private
    }

    public static Predicate<Match> isEenMatchDieVandaagIs() {
        return match -> match.matchTime().isAfter(LocalDateTime.now().toLocalDate().atStartOfDay()) &&
                match.matchTime().isBefore(LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
    }
}