package nl.dejagermc.homefeeder.reporting.telegram;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Telegram {
    private static final String BASE = "https://api.telegram.org";
    private static final String BOT = "/bot";
    private static final String CHANNEL = "/sendMessage?chat_id=";
    private static final String MESSAGE = "&text=";

    @Value("${telegram.bot.name}")
    private String botName;
    @Value("${telegram.channel.name}")
    private String channelName;

    public Telegram() {
    }

    public void sendMessage(String message) {
        doSendMessage(message);
    }

    private void doSendMessage(String message) {
        try {
            Document doc = Jsoup.connect(createUrl(botName, channelName, message)).ignoreContentType(true).get();
            handleTelegramResponse(doc.body().text());
        } catch (Exception e) {
            log.error("Error sending message to telegram: {}", e);
        }
    }

    private void handleTelegramResponse(String response) {
        if (response.matches(".*\"ok\":true.*")) {
            log.info("Telegram message: ok");
        } else {
            log.warn("Telegram message: not ok: {}", response);
        }
    }

    private String createUrl(String botName, String channelName, String message) {
        return new StringBuilder()
                .append(BASE)
                .append(BOT)
                .append(botName)
                .append(CHANNEL)
                .append(channelName)
                .append(MESSAGE)
                .append(message)
                .toString();
    }
}
