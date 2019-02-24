package nl.dejagermc.homefeeder.input.liquipedia.dota.builders;

import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;

import java.time.LocalDateTime;

public class TournamentBuilders {
    public static Tournament defaultTournament(String name, TournamentType tournamentType, boolean active,
                                               boolean byValve) {
        LocalDateTime time;
        if (active) {
            time = LocalDateTime.now();
        } else {
            time = LocalDateTime.now().plusMonths(2);
        }

        return Tournament.builder()
                .isByValve(byValve)
                .start(time.minusDays(5))
                .end(time.plusDays(5))
                .name(name)
                .teams(2)
                .location("London")
                .prize(5)
                .tournamentType(tournamentType)
                .winner("")
                .build();
    }
}
