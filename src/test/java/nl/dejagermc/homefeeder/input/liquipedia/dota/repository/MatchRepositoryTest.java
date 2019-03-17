package nl.dejagermc.homefeeder.input.liquipedia.dota.repository;

import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MatchRepository.class)
@EnableConfigurationProperties
public class MatchRepositoryTest {

    private static final String UPCOMING_AND_ONGOING_MATCHES_URI = "https://liquipedia.net/dota2/Liquipedia:Upcoming_and_ongoing_matches";

    @MockBean
    private HttpUtil httpUtil;

    @Autowired
    private MatchRepository matchRepository;

    @Test
    public void testOphalenMatches() {
        // 4 matches, 1 live
        String matches1 = readFileToString("src/test/resources/input/liquipedia/dota/matches1.html");
        // 4 matches, 2 live
        String matches2 = readFileToString("src/test/resources/input/liquipedia/dota/matches2.html");
        // 3 matches, 1 live, 1 previous live removed
        String matches3 = readFileToString("src/test/resources/input/liquipedia/dota/matches3.html");

        matches1 = setDatesMatches1(matches1);
        matches2 = setDatesMatches2(matches2);
        matches3 = setDatesMatches3(matches3);

        Document docMatches1 = Jsoup.parseBodyFragment(matches1);
        Document docMatches2 = Jsoup.parseBodyFragment(matches2);
        Document docMatches3 = Jsoup.parseBodyFragment(matches3);

        Mockito.when(httpUtil.getDocument(UPCOMING_AND_ONGOING_MATCHES_URI)).thenReturn(Optional.of(docMatches1));
        Set<Match> matches1Result = matchRepository.getAllMatches();
        assert (matches1Result.size() == 4);
        assert (matches1Result.stream().filter(Match::isLive).count() == 1L);
        assert (matches1Result.stream().anyMatch(m -> m.matchEitherTeam("WG.U")));

        Mockito.when(httpUtil.getDocument(UPCOMING_AND_ONGOING_MATCHES_URI)).thenReturn(Optional.of(docMatches2));
        Set<Match> matches2Result = matchRepository.getAllMatches();
        assert (matches2Result.size() == 4);
        assert (matches2Result.stream().filter(Match::isLive).count() == 2L);

        Mockito.when(httpUtil.getDocument(UPCOMING_AND_ONGOING_MATCHES_URI)).thenReturn(Optional.of(docMatches3));
        Set<Match> matches3Result = matchRepository.getAllMatches();
        assert (matches3Result.size() == 3);
        assert (matches3Result.stream().filter(Match::isLive).count() == 1L);
        assert (matches3Result.stream().noneMatch(m -> m.matchEitherTeam("WG.U")));

        // TODO Hamcrest toevoegen voor checken dat lijst bepaalde items bevat
//        org.hamcrest.CoreMatchers.
//        Matcher<Match> match1Matcher = Builder
//        assertThat(matches1Result, containsInAnyOrder());

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

    private String setDatesMatches1(String matches) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy - H:mm", Locale.US);
        matches = matches.replace("countdown_match1_live", LocalDateTime.now().minusHours(1).format(formatter));
        matches = matches.replace("countdown_match2", LocalDateTime.now().plusHours(1).format(formatter));
        matches = matches.replace("countdown_match3", LocalDateTime.now().plusHours(3).format(formatter));
        matches = matches.replace("countdown_match4", LocalDateTime.now().plusHours(5).format(formatter));
        return matches;
    }

    private String setDatesMatches2(String matches) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy - H:mm", Locale.US);
        matches = matches.replace("countdown_match1_live", LocalDateTime.now().minusHours(1).format(formatter));
        matches = matches.replace("countdown_match2", LocalDateTime.now().minusHours(1).format(formatter));
        matches = matches.replace("countdown_match3", LocalDateTime.now().plusHours(3).format(formatter));
        matches = matches.replace("countdown_match4", LocalDateTime.now().plusHours(5).format(formatter));
        return matches;
    }

    private String setDatesMatches3(String matches) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy - H:mm", Locale.US);
        matches = matches.replace("countdown_match2", LocalDateTime.now().minusHours(1).format(formatter));
        matches = matches.replace("countdown_match3", LocalDateTime.now().plusHours(3).format(formatter));
        matches = matches.replace("countdown_match4", LocalDateTime.now().plusHours(5).format(formatter));
        return matches;
    }

}
