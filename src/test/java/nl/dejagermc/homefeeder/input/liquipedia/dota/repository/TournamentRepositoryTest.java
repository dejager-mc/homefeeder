package nl.dejagermc.homefeeder.input.liquipedia.dota.repository;

import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TournamentRepository.class)
@EnableConfigurationProperties
public class TournamentRepositoryTest {
    private static final String URI_PREMIER = "https://liquipedia.net/dota2/Premier_Tournaments";

    @MockBean
    private JsoupUtil jsoupUtil;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Test
    public void testPremierTournament() {
        String premierTournaments = readFileToString("src/test/resources/input/liquipedia/dota/tournaments.premier.html");
        premierTournaments = setDatesTournaments(premierTournaments);
        Document docPremierTournaments = Jsoup.parseBodyFragment(premierTournaments);

        Mockito.when(jsoupUtil.getDocument(URI_PREMIER)).thenReturn(Optional.of(docPremierTournaments));
        Set<Tournament> premierTournamentsList = tournamentRepository.getAllPremierTournaments();
        assertEquals(2, premierTournamentsList.size());
    }

    private String readFileToString(String path) {
        try (BufferedReader r = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            r.lines().forEach(line -> sb.append(line));
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String setDatesTournaments(String tournaments) {
        // tournament1 = live, same day
        // tournament2 = future, same month
        // tournament3 = past, different month

        DateTimeFormatter formatterSameDay = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);
        DateTimeFormatter formatMonth = DateTimeFormatter.ofPattern("MMM", Locale.US);
        DateTimeFormatter formatDay = DateTimeFormatter.ofPattern("d", Locale.US);
        DateTimeFormatter formatYear = DateTimeFormatter.ofPattern("yyyy", Locale.US);

        LocalDateTime futureMonthStart = LocalDateTime.now().plusMonths(2).withDayOfMonth(2);
        LocalDateTime futureMonthEnd = futureMonthStart.plusDays(5);
        String futureSameMonthDateTournament2 =
                futureMonthStart.format(formatMonth) + " " +
                futureMonthStart.format(formatDay) + " - " +
                futureMonthEnd.format(formatDay) + ", " +
                futureMonthStart.format(formatYear);

        LocalDateTime pastMonth1Start = LocalDateTime.of(2017,1,1,0,0,0);
        LocalDateTime pastMonth2End = LocalDateTime.of(2017,3,1,0,0,0);
        String pastDifferentMonthsTournament3 = pastMonth1Start.format(formatMonth) + " " +
                pastMonth1Start.format(formatDay) + " - " +
                pastMonth2End.format(formatMonth) + " " +
                pastMonth2End.format(formatDay) + ", " +
                pastMonth1Start.format(formatYear);

        tournaments = tournaments.replace("dateTournament1", LocalDateTime.now().format(formatterSameDay));
        tournaments = tournaments.replace("dateTournament2", futureSameMonthDateTournament2);
        tournaments = tournaments.replace("dateTournament3", pastDifferentMonthsTournament3);

        return tournaments;
    }
}
