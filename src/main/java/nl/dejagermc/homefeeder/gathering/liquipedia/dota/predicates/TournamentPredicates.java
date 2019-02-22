package nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Predicate;

public class TournamentPredicates {
    private TournamentPredicates() {
        // private
    }

    public static Predicate<Tournament> isTournamentActive() {
        return t -> t.start().isBefore(LocalDateTime.now()) &&
                t.end().isAfter(LocalDateTime.now());
    }

    public static Comparator<Tournament> sortTournamentsByImportanceMostToLeast() {
        return Comparator
                .comparing(Tournament::isByValve)
                .thenComparing(Tournament::isPremier)
                .thenComparing(Tournament::isMajor)
                .thenComparing(Tournament::isQualifier)
                .thenComparing(t -> t.name().toLowerCase().matches(".*europe.*"))
                .reversed();
    }
}
