package nl.dejagermc.homefeeder.reporting.reported;

import android.util.Pair;
import nl.dejagermc.homefeeder.domain.generated.Episode;
import nl.dejagermc.homefeeder.domain.generated.Movie;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.reporting.reported.model.ReportedTo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportedService {
    private List<Pair<Match, ReportedTo>> matches;
    private List<Pair<Tournament, ReportedTo>> tournaments;

    private List<Pair<Episode, ReportedTo>> episodes;
    private List<Pair<Movie, ReportedTo>> movies;

    public boolean hasThisBeenReported(Object object, ReportedTo reportedTo) {
        if (object instanceof Match) {
            return matches.stream().filter(m -> m.first.equals(object) && m.second.equals(reportedTo)).findAny().isPresent();
        }
        else if (object instanceof Tournament) {
            return tournaments.stream().filter(m -> m.first.equals(object) && m.second.equals(reportedTo)).findAny().isPresent();
        }
        else if (object instanceof Episode) {
            return episodes.stream().filter(m -> m.first.equals(object) && m.second.equals(reportedTo)).findAny().isPresent();
        }
        else if (object instanceof Movie) {
            return movies.stream().filter(m -> m.first.equals(object) && m.second.equals(reportedTo)).findAny().isPresent();
        }
        return false;
    }

    public void reportThisToThat(Object object, ReportedTo reportedTo) {
        if (object instanceof Match) {
            matches.add(new Pair(object, reportedTo));
        } else if (object instanceof Tournament) {
            tournaments.add(new Pair(object, reportedTo));
        }
        else if (object instanceof Episode) {
            episodes.add(new Pair(object, reportedTo));
        }
        else if (object instanceof Movie) {
            movies.add(new Pair(object, reportedTo));
        }
    }
}
