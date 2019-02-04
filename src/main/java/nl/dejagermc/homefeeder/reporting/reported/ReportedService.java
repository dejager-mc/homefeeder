package nl.dejagermc.homefeeder.reporting.reported;

import nl.dejagermc.homefeeder.domain.generated.Episode;
import nl.dejagermc.homefeeder.domain.generated.Movie;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.reporting.reported.model.ReportedTo;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportedService {
    private List<Pair<Match, ReportedTo>> matches = new ArrayList<>();
    private List<Pair<Tournament, ReportedTo>> tournaments = new ArrayList<>();

    private List<Pair<Episode, ReportedTo>> episodes = new ArrayList<>();
    private List<Pair<Movie, ReportedTo>> movies = new ArrayList<>();

    public boolean hasThisBeenReported(Object object, ReportedTo reportedTo) {
        if (object instanceof Match) {
            return matches.stream().filter(m -> m.getFirst().equals(object) && m.getSecond().equals(reportedTo)).findAny().isPresent();
        }
        else if (object instanceof Tournament) {
            return tournaments.stream().filter(m -> m.getFirst().equals(object) && m.getSecond().equals(reportedTo)).findAny().isPresent();
        }
        else if (object instanceof Episode) {
            return episodes.stream().filter(m -> m.getFirst().equals(object) && m.getSecond().equals(reportedTo)).findAny().isPresent();
        }
        else if (object instanceof Movie) {
            return movies.stream().filter(m -> m.getFirst().equals(object) && m.getSecond().equals(reportedTo)).findAny().isPresent();
        }
        return false;
    }

    public void reportThisToThat(Object object, ReportedTo reportedTo) {
        if (object instanceof Match) {
            matches.add(Pair.of((Match)object, reportedTo));
        } else if (object instanceof Tournament) {
            tournaments.add(Pair.of((Tournament)object, reportedTo));
        }
        else if (object instanceof Episode) {
            episodes.add(Pair.of((Episode)object, reportedTo));
        }
        else if (object instanceof Movie) {
            movies.add(Pair.of((Movie)object, reportedTo));
        }
    }
}
