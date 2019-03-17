package nl.dejagermc.homefeeder.output.telegram;

import ch.qos.logback.classic.Level;
import nl.dejagermc.homefeeder.testrules.LoggerRule;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TelegramOutputService.class})
@EnableConfigurationProperties
public class TelegramOutputServiceTest {

    @Autowired
    private TelegramOutputService telegramOutputService;

    @Rule
    public final LoggerRule loggerRule = new LoggerRule();

    @MockBean
    private HttpUtil httpUtil;

    @Test
    public void testSendMesageNoResponseServer() {
        when(httpUtil.getDocumentIgnoreContentType(anyString())).thenReturn(Optional.empty());
        telegramOutputService.sendMessage("Test");
        validateMockitoUsage();
        assertThat(loggerRule.getFormattedMessages().size()).isEqualTo(1);
        assertThat(loggerRule.getFormattedMessages().get(0)).isEqualTo("Telegram: no response from server.");
        assertThat(loggerRule.getLoggingEvents().get(0).getLevel()).isEqualTo(Level.ERROR);
    }

    @Test
    public void testSendMesageOkResponseServer() {
        String message = "\"ok\":true";
        Document document = Jsoup.parseBodyFragment(message);
        when(httpUtil.getDocumentIgnoreContentType(anyString())).thenReturn(Optional.of(document));
        telegramOutputService.sendMessage("Test");
        validateMockitoUsage();
        assertThat(loggerRule.getFormattedMessages().size()).isEqualTo(1);
        assertThat(loggerRule.getFormattedMessages().get(0)).isEqualTo("Telegram: message send");
        assertThat(loggerRule.getLoggingEvents().get(0).getLevel()).isEqualTo(Level.INFO);
    }

    @Test
    public void testSendMesageNotOkResponseServer() {
        String message = "\"ok\":false";
        Document document = Jsoup.parseBodyFragment(message);
        when(httpUtil.getDocumentIgnoreContentType(anyString())).thenReturn(Optional.of(document));
        telegramOutputService.sendMessage("Test");
        validateMockitoUsage();
        assertThat(loggerRule.getFormattedMessages().size()).isEqualTo(1);
        assertThat(loggerRule.getFormattedMessages().get(0)).isEqualTo("Telegram: message send with message: \"ok\":false");
        assertThat(loggerRule.getLoggingEvents().get(0).getLevel()).isEqualTo(Level.ERROR);
    }

    @Test
    public void testSendEmptyMesage() {
        telegramOutputService.sendMessage("");
        validateMockitoUsage();
        verify(httpUtil, times(0)).getDocumentIgnoreContentType(anyString());
    }
}
