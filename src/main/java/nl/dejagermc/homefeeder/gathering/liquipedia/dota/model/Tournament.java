package nl.dejagermc.homefeeder.gathering.liquipedia.dota.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(fluent = true)
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Tournament {
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private int prize;
    private int teams;
    private String winner;
    private String location;
    private boolean isByValve;
    private TournamentType tournamentType;

    public boolean isActive() {
        return (start.isBefore(LocalDateTime.now()) && end.isAfter(LocalDateTime.now()));
    }

    public boolean isPremier() {
        return (tournamentType.equals(TournamentType.PREMIER));
    }

    public boolean isMajor() {
        return (tournamentType.equals(TournamentType.MAJOR));
    }

    public boolean isQualifier() {
        return (tournamentType.equals(TournamentType.QUALIFIER));
    }
}