package nl.dejagermc.homefeeder.gathering.liquipedia.dota;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.UpcomingAndOngoingMatches;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private static final Logger LOG = LoggerFactory.getLogger(MatchService.class);

    private UpcomingAndOngoingMatches repository;

    @Autowired
    public MatchService(UpcomingAndOngoingMatches repository) {
        this.repository = repository;
    }

    public List<Match> getAllMatches() {
        return repository.getAllMatches();
    }

    public Optional<Match> getNextMatchForTeam(String team) {
        List<Match> allMatches = repository.getAllMatches();
        return allMatches.stream()
                .sorted(Comparator.comparing(Match::matchTime))
                .filter(match -> match.matchEitherTeam(team))
                .findFirst();
    }

    public List<Match> getLiveMatches() {
        return repository.getAllMatches().stream()
                .filter(Match::isLive)
                .collect(Collectors.toList());
    }
}
