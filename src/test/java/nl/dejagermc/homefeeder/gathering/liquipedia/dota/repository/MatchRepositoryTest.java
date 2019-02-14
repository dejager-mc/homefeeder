package nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository;

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
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MatchRepository.class)
@EnableConfigurationProperties
public class MatchRepositoryTest {

    @MockBean
    private JsoupUtil jsoupUtil;

    @Autowired
    private MatchRepository matchRepository;

    @Test
    public void testOphalenMatches() throws IOException {
//        File oldMatches = new File("src/test/resources/gathering/liquipedia/dota/MatchesOld.html");
//        File newMatches = new File("src/test/resources/gathering/liquipedia/dota/newMatches.html");
//        Document doc = Jsoup.parse(oldMatches, "UTF-8", "");

        String oldMatches = readFileToString("src/test/resources/gathering/liquipedia/dota/MatchesOld.html");
        oldMatches = setDatesInOldMatchesFile(oldMatches);

        Document doc = Jsoup.parseBodyFragment(oldMatches);

        Mockito.when(jsoupUtil.getDocument(Mockito.anyString())).thenReturn(Optional.of(doc));
        matchRepository.getAllMatches();
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

    private String setDatesInOldMatchesFile(String oldMatches) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy - H:mm", Locale.US);
        String dateTimeLive = LocalDateTime.now().minusHours(1).format(formatter);
        oldMatches = oldMatches.replace("countdown_match1_live", dateTimeLive);
        oldMatches = oldMatches.replace("countdown_match2", LocalDateTime.now().plusHours(1).format(formatter));
        oldMatches = oldMatches.replace("countdown_match3", LocalDateTime.now().plusHours(3).format(formatter));
        oldMatches = oldMatches.replace("countdown_match4", LocalDateTime.now().plusHours(5).format(formatter));
        return oldMatches;
    }


}
