package nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        String premierTournaments = readFileToString("src/test/resources/gathering/liquipedia/dota/tournaments.premier.html");
        Document docPremierTournaments = Jsoup.parseBodyFragment(premierTournaments);

        Mockito.when(jsoupUtil.getDocument(URI_PREMIER)).thenReturn(Optional.of(docPremierTournaments));
        Set<Tournament> premierTournamentsList = tournamentRepository.getAllPremierTournaments();
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
}
