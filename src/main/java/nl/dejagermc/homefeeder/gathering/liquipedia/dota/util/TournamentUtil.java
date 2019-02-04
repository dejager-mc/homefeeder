package nl.dejagermc.homefeeder.gathering.liquipedia.dota.util;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TournamentUtil {

    public static List<Tournament> getActiveTournaments(List<Tournament> tournaments) {
        return tournaments.stream()
                .filter(t -> t.start().isBefore(LocalDateTime.now()))
                .filter(t -> t.end().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Tournament::start))
                .collect(Collectors.toList());
    }

    public static Optional<Tournament> getMostImportantActiveTournament(List<Tournament> tournaments) {
        return tournaments.stream()
                .filter(t -> t.start().isBefore(LocalDateTime.now()))
                .filter(t -> t.end().isAfter(LocalDateTime.now()))
                .sorted(Comparator
                        .comparing(Tournament::start)
                        .thenComparing(Tournament::isByValve)
                        .thenComparing(Tournament::isPremier)
                        .thenComparing(Tournament::isMajor)
                        .thenComparing(Tournament::isQualifier)
                )
                .findFirst();

    }
}
