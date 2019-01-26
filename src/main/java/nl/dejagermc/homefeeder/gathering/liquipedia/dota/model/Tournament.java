package nl.dejagermc.homefeeder.gathering.liquipedia.dota.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Accessors(fluent = true)
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Tournament {
    private String name;
    private LocalDate start;
    private LocalDate end;
    private int prize;
    private int teams;
    private String winner;
    private String location;
    private boolean byValve;
}
