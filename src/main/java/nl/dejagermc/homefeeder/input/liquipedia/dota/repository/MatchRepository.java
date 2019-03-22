package nl.dejagermc.homefeeder.input.liquipedia.dota.repository;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MatchRepository {
    private static final String UPCOMING_AND_ONGOING_MATCHES_URI = "https://liquipedia.net/dota2/Liquipedia:Upcoming_and_ongoing_matches";
    private static final String UNKNOWN_TEAM = "T.B.D.";
    private static final String BASE_URI = "https://liquipedia.net";

    private Set<Match> oldMatches = new HashSet<>();

    private HttpUtil httpUtil;

    @Autowired
    public MatchRepository(HttpUtil httpUtil) {
        this.httpUtil = httpUtil;
    }

    @Cacheable(cacheNames = "getAllMatches", cacheManager = "cacheManagerCaffeine")
    public Set<Match> getAllMatches() {
        log.info("UC110: get all matches.");
        Elements elements = getAllMatchElements();
        Set<Match> newMatches = convertElementsToMatches(elements);
        return mergeNewMatchesWithExistingMatches(newMatches);
    }

    private Set<Match> mergeNewMatchesWithExistingMatches(Set<Match> newMatches) {
        // remove TBD matches
        // remove old matches not in new matches
        Set<Match> oldMatchesExpiredMatchesRemoved = oldMatches.stream()
                .filter(m -> !m.matchEitherTeam(UNKNOWN_TEAM))
                .filter(newMatches::contains)
                .collect(Collectors.toSet());
        // add new matches not in old matches
        Set<Match> actualNewMatches = newMatches.stream()
                .filter(m -> !oldMatchesExpiredMatchesRemoved.contains(m))
                .collect(Collectors.toSet());

        oldMatches = new HashSet<>();
        oldMatches.addAll(oldMatchesExpiredMatchesRemoved);
        oldMatches.addAll(actualNewMatches);

        return oldMatches;
    }

    @Cacheable(cacheNames = "getFullTournamentName", cacheManager = "cacheManagerCaffeine")
    public String getFullTournamentName(Element element) {
        log.info("UC111: get full tournament name.");
        String url = BASE_URI + element.select("td.match-filler").select("div").select("div").select("a").attr("href");
        Optional<Document> optionalDoc = httpUtil.getDocument(url);
        if (optionalDoc.isPresent()) {
            return optionalDoc.get().select("h1.firstHeading").select("span").text();
        }
        return "";
    }

    private Set<Match> convertElementsToMatches(Elements elements) {
        return elements.stream()
                .filter(Objects::nonNull)
                .map(this::createMatch)
                .collect(Collectors.toSet());
    }

    private Match createMatch(Element element) {
        return Match.builder()
                .leftTeam(getLeftTeam(element))
                .rightTeam(getRightTeam(element))
                .gameType(getGameType(element))
                .tournamentName(getFullTournamentName(element))
                .twitchChannel(getTwitchChannel(element))
                .youtubeChannel(getYoutubeChannel(element))
                .matchTime(getMatchTime(element))
                .build();
    }

    private Elements getAllMatchElements() {
        Optional<Document> optionalDoc = httpUtil.getDocument(UPCOMING_AND_ONGOING_MATCHES_URI);
        if (optionalDoc.isPresent()) {
            return optionalDoc.get().select("div > table");
        }

        return new Elements();
    }

    private LocalDateTime getMatchTime(Element element) {
        String timeTillStart = getTimeTillStart(element);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy - H:mm z", Locale.US);
            return LocalDateTime.parse(timeTillStart, formatter).plusHours(1L);
        } catch (Exception e) {
            log.warn("Error parsing match time {}. Returning with year 2100.", timeTillStart);
        }

        return LocalDateTime.of(2100, 1, 1, 0, 0, 0);
    }

    private String getLeftTeam(Element element) {
        String team = element.select("td.team-left").select("span").select("span").select("a").text().trim();
        return team.isBlank() ? UNKNOWN_TEAM : team;
    }

    private String getRightTeam(Element element) {
        String team = element.select("td.team-right").select("span").select("span").select("a").text().trim();
        return team.isBlank() ? UNKNOWN_TEAM : team;
    }

    private String getGameType(Element element) {
        return element.select("td.versus").select("div").select("abbr").text();
    }

    private String getTimeTillStart(Element element) {
        return element.select("span.timer-object-countdown-only").text();
    }

    private String getTwitchChannel(Element element) {
        return element.select("span[data-stream-twitch]").attr("data-stream-twitch");
    }

    private String getYoutubeChannel(Element element) {
        return element.select("span[data-stream-youtube]").attr("data-stream-youtube");
    }
}
