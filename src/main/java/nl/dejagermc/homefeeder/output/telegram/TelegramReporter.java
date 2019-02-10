package nl.dejagermc.homefeeder.output.telegram;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.user.UserState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

@Service
@Slf4j
public class TelegramReporter {
    private static final String BASE = "https://api.telegram.org";
    private static final String BOT = "/bot";
    private static final String CHANNEL = "/sendMessage?chat_id=";
    private static final String MESSAGE = "&text=";
    private static final String PARSE_MODE = "&parse_mode=html";

    @Value("${telegram.bot.name}")
    private String botName;
    @Value("${telegram.channel.name}")
    private String channelName;

    private UserState userState;

    @Autowired
    public TelegramReporter(UserState userState) {
        this.userState = userState;
    }

    public void sendMessage(String message) {
        if (userState.useTelegram()) {
            doSendMessage(message);
        }
    }

    private void doSendMessage(String message) {
        try {
            String encodedMessage = URLEncoder.encode(message, "UTF-8");
            Document doc = Jsoup.connect(createUrl(botName, channelName, encodedMessage)).ignoreContentType(true).get();
            handleTelegramResponse(doc.body().text());
        } catch (Exception e) {
            log.error("Error sending message to telegram: {}", e);
        }
    }

    private void handleTelegramResponse(String response) {
        if (response.matches(".*\"ok\":true.*")) {
            log.info("Telegram message: ok");
        } else {
            log.error("Telegram message: not ok: {}", response);
        }
    }

    private String createUrl(String botName, String channelName, String message) {
        return new StringBuilder()
                .append(BASE)
                .append(BOT)
                .append(botName)
                .append(CHANNEL)
                .append(channelName)
                .append(PARSE_MODE)
                .append(MESSAGE)
                .append(message)
                .toString();
    }
}
