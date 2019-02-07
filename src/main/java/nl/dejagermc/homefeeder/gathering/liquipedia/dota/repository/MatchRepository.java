package nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
    private static final String BASE_URI = "https://liquipedia.net";

    private List<Match> oldMatches = new ArrayList<>();

    @Cacheable(cacheNames = "getAllMatches", cacheManager = "cacheManagerCaffeine")
    public List<Match> getAllMatches() {
        Elements elements = getAllMatchElements();
        List<Match> newMatches = convertElementsToMatches(elements);
        return mergeNewMatchesWithExcistingMatches(newMatches);
    }

    private List<Match> mergeNewMatchesWithExcistingMatches(List<Match> newMatches) {
        // remove TBD matches
        // remove old matches not in new matches
        List<Match> oldMatchesExpiredMatchesRemoved = oldMatches.stream()
                .filter(m -> !m.matchEitherTeam("T.B.D."))
                .filter(m -> newMatches.contains(m))
                .collect(Collectors.toList());
        // add new matches not in old matches
        List<Match> actualNewMatches = newMatches.stream()
                .filter(m -> !oldMatchesExpiredMatchesRemoved.contains(m))
                .collect(Collectors.toList());

        oldMatches = new ArrayList<>();
        oldMatches.addAll(oldMatchesExpiredMatchesRemoved);
        oldMatches.addAll(actualNewMatches);

        return oldMatches;
    }

    @Cacheable(cacheNames = "getFullTournamentName", cacheManager = "cacheManagerCaffeine")
    public String getFullTournamentName(Element element) {
        String url = BASE_URI + element.select("td.match-filler").select("div").select("div").select("a").attr("href");
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.select("h1.firstHeading").select("span").text();
        } catch (Exception e) {
            log.error("Liquipedia get request error: ", e);
        }
        return "";
    }

    private List<Match> convertElementsToMatches(Elements elements) {
        return elements.stream()
                .filter(Objects::nonNull)
                .map(this::createMatch)
                .collect(Collectors.toList());
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
        try {
            Document doc = Jsoup.connect(UPCOMING_AND_ONGOING_MATCHES_URI).get();
            return doc.select("div > table");
        } catch (Exception e) {
            log.error("Liquipedia get request error: ", e);
            return new Elements();
        }
    }

    private LocalDateTime getMatchTime(Element element) {
        String timeTillStart = getTimeTillStart(element);
        try {
            Locale us = Locale.US;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy - H:mm z", us);
            return LocalDateTime.parse(timeTillStart, formatter).plusHours(1L);
        } catch (Exception e) {
            log.warn("Error parsing match time {}. Returning with year 2100.", timeTillStart);
        }

        return LocalDateTime.of(2100,1,1,0,0,0);
    }

    private String getLeftTeam(Element element) {
        String team = element.select("td.team-left").select("span").select("span").select("a").text().trim();
        return team.isBlank() ? "T.B.D." : team;
    }

    private String getRightTeam(Element element) {
        String team = element.select("td.team-right").select("span").select("span").select("a").text().trim();
        return team.isBlank() ? "T.B.D." : team;
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
