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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UpcomingAndOngoingMatches {
    private static final String UPCOMING_AND_ONGOING_MATCHES_URI = "https://liquipedia.net/dota2/Liquipedia:Upcoming_and_ongoing_matches";

    @Cacheable(cacheNames = "getAllMatches", cacheManager = "cacheManagerCaffeine")
    public List<Match> getAllMatches() {
        Elements elements = getAllMatchElements();
        return convertElementsToMatches(elements);
    }

    private List<Match> convertElementsToMatches(Elements elements) {
        return elements.stream()
                .filter(Objects::nonNull)
                .map(e -> createMatch(e))
                .collect(Collectors.toList());
    }

    private Match createMatch(Element element) {
        return Match.builder()
                .leftTeam(getLeftTeam(element))
                .rightTeam(getRightTeam(element))
                .gameType(getGameType(element))
                .eventName(getEventName(element))
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
            log.error("Liquipedia get request error: " + e);
            return new Elements();
        }
    }

    private LocalDateTime getMatchTime(Element element) {
        String timeTillStart = getTimeTillStart(element);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy - H:mm z");
            return LocalDateTime.parse(timeTillStart, formatter);
        } catch (Exception e) {
            log.warn("Error parsing match time {}. Returning with year 2100.", timeTillStart);
        }

        return LocalDateTime.of(2100,1,1,0,0,0);
    }

    private String getLeftTeam(Element element) {
        return element.select("td.team-left").select("span").select("span").select("a").text().trim();
    }

    private String getRightTeam(Element element) {
        return element.select("td.team-right").select("span").select("span").select("a").text().trim();
    }

    private String getGameType(Element element) {
        return element.select("td.versus").select("div").select("abbr").text();
    }

    private String getTimeTillStart(Element element) {
        return element.select("span.timer-object-countdown-only").text();
    }

    private String getEventName(Element element) {
        return element.select("td.match-filler").select("div").select("div").select("a").text();
    }

    private String getTwitchChannel(Element element) {
        return element.select("span[data-stream-twitch]").attr("data-stream-twitch");
    }

    private String getYoutubeChannel(Element element) {
        return element.select("span[data-stream-youtube]").attr("data-stream-youtube");
    }
}
