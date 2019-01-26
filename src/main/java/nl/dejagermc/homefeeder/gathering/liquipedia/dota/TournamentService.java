package nl.dejagermc.homefeeder.gathering.liquipedia.dota;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.PremierEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentService {

    private PremierEvents premierEvents;

    @Autowired
    public TournamentService(PremierEvents premierEvents) {
        this.premierEvents = premierEvents;
    }

    public List<Tournament> getAllTournaments() {
        List allTournaments = premierEvents.getAllPremierEvents();
        allTournaments.addAll(premierEvents.getAllMajorEvents());
        return allTournaments;
    }
}
