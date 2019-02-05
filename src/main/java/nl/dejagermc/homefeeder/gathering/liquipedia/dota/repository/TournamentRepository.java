package nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.TournamentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TournamentRepository {

    private static final String URI_PREMIER = "https://liquipedia.net/dota2/Premier_Tournaments";
    private static final String URI_MAJOR = "https://liquipedia.net/dota2/Major_Tournaments";
    private static final String URI_QUALIFIERS = "https://liquipedia.net/dota2/Qualifier_Tournaments";

    @Cacheable(cacheNames = "getAllPremierTournaments", cacheManager = "cacheManagerCaffeine")
    public List<Tournament> getAllPremierTournaments() {
        return getAllEvents(URI_PREMIER).stream().map(e -> convertElementToTournament(e, TournamentType.PREMIER)).collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "getAllMajorTournaments", cacheManager = "cacheManagerCaffeine")
    public List<Tournament> getAllMajorTournaments() {
        return getAllEvents(URI_MAJOR).stream().map(e -> convertElementToTournament(e, TournamentType.MAJOR)).collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "getAllQualifierTournaments", cacheManager = "cacheManagerCaffeine")
    public List<Tournament> getAllQualifierTournaments() {
        return getAllEvents(URI_QUALIFIERS).stream().map(e -> convertElementToTournament(e, TournamentType.QUALIFIER)).collect(Collectors.toList());
    }

    private Elements getAllEvents(String uri) {
        try {
            Document doc = Jsoup.connect(uri).get();
            return doc.select("div.divRow");
        } catch (Exception e) {
            log.error("Liquipedia get request error: " + e);
            return new Elements();
        }
    }

    private Tournament convertElementToTournament(Element element, TournamentType tournamentType) {
        String date = getTournamentDetail(element, "Date");
        List<LocalDateTime> dates = dateStringToPeriod(date);

        return Tournament.builder()
                .start(dates.get(0))
                .end(dates.get(1))
                .name(getTournamentDetail(element, "Tournament"))
                .prize(prizeToInt(getTournamentDetail(element, "Prize")))
                .teams(teamsToInt(getTournamentDetail(element, "PlayerNumber")))
                .winner(getTournamentDetail(element, "FirstPlace"))
                .location(getTournamentDetail(element, "Location"))
                .isByValve(isTournamentByValve(element))
                .tournamentType(tournamentType)
                .build();
    }

    private int prizeToInt(String prize) {
        if (prize.isBlank()) {
            return 0;
        }
        return Integer.parseInt(prize.substring(1).replaceAll(",", ""));
    }

    private int teamsToInt(String teams) {
        if (teams.isBlank()) {
            return 0;
        }
        return Integer.parseInt(teams.split(" ")[0]);
    }

    private boolean isTournamentByValve(Element element) {
        Elements list = element.select("div.Header-Premier");
        return !list.isEmpty();
    }

    private List<LocalDateTime> dateStringToPeriod(String date) {
        String year1 = "";
        String year2 = "";
        String month1 = "";
        String month2 = "";
        String day1 = "";
        String day2 = "";

        String patternSameDayString = "(.{3}) (\\d+), (\\d{4})";
        String patternSameMonthString = "(.{3}) (\\d+) - (\\d+), (\\d{4})";
        String patternDifferentMonthString = "(.{3}) (\\d+) - (.{3}) (\\d+), (\\d{4})";
        String patternDifferentYearString = "(.{3}) (\\d+), (\\d+) - (.{3}) (\\d+), (\\d+)";

        Pattern patternSameDay = Pattern.compile(patternSameDayString);
        Pattern patternSameMonth = Pattern.compile(patternSameMonthString);
        Pattern patternDifferentMonth = Pattern.compile(patternDifferentMonthString);
        Pattern patternDifferentYear = Pattern.compile(patternDifferentYearString);
        Matcher matcherSameDay = patternSameDay.matcher(date);
        Matcher matcherSameMonth = patternSameMonth.matcher(date);
        Matcher matcherDifferentMonth = patternDifferentMonth.matcher(date);
        Matcher matcherDifferentYear = patternDifferentYear.matcher(date);


        if (matcherSameDay.matches()) {
            year1 = matcherSameDay.group(3);
            year2 = year1;
            month1 = matcherSameDay.group(1);
            month2 = month1;
            day1 = matcherSameDay.group(2);
            day2 = day1;
        } else if (matcherSameMonth.matches()) {
            year1 = matcherSameMonth.group(4);
            year2 = year1;
            month1 = matcherSameMonth.group(1);
            month2 = month1;
            day1 = matcherSameMonth.group(2);
            day2 = matcherSameMonth.group(3);
        } else if (matcherDifferentMonth.matches()) {
            year1 = matcherDifferentMonth.group(5);
            year2 = year1;
            month1 = matcherDifferentMonth.group(1);
            month2 = matcherDifferentMonth.group(3);
            day1 = matcherDifferentMonth.group(2);
            day2 = matcherDifferentMonth.group(4);
        } else if (matcherDifferentYear.matches()) {
            year1 = matcherDifferentYear.group(3);
            year2 = matcherDifferentYear.group(6);
            month1 = matcherDifferentYear.group(1);
            month2 = matcherDifferentYear.group(4);
            day1 = matcherDifferentYear.group(2);
            day2 = matcherDifferentYear.group(5);
        } else {
            Assert.notNull(null, "Geen formatter gevonden voor date: " + date);
        }

        LocalDateTime start = parseDate(month1, day1, year1).atStartOfDay();
        LocalDateTime end = parseDate(month2, day2, year2).plusDays(1L).atStartOfDay();
        return Arrays.asList(start, end);
    }

    private LocalDate parseDate(String month, String day, String year) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");
        String parseString = String.format("%s %s %s", month, day, year);
        return LocalDate.parse(parseString, formatter);
    }

    private String getTournamentDetail(Element element, String detail) {
        return element.select("div." + detail).text().trim();
    }
}
