package nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class TournamentPredicates {
    private TournamentPredicates() {
        // private
    }

    public static Predicate<Tournament> isTournamentActief() {
        return t -> t.start().isBefore(LocalDateTime.now()) &&
                t.end().isAfter(LocalDateTime.now());
    }
}
