package nl.dejagermc.homefeeder.output.telegram;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@Slf4j
public class TelegramOutputService {
    private static final String BASE = "https://api.telegram.org";
    private static final String BOT_BASE = "/bot";
    private static final String CHANNEL_BASE = "/sendMessage?chat_id=";
    private static final String MESSAGE_BASE = "&text=";
    private static final String PARSE_MODE = "&parse_mode=html";

    @Value("${telegram.bot.name}")
    private String botName;
    @Value("${telegram.channel.name}")
    private String channelName;

    private HttpUtil httpUtil;

    @Autowired
    public TelegramOutputService(HttpUtil httpUtil) {
        this.httpUtil = httpUtil;
    }

    public void sendMessage(String message) {
        if (message.isBlank()) {
            return;
        }
        doSendMessage(message);
    }

    private void doSendMessage(String message) {
        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
            httpUtil.getDocumentIgnoreContentType(createUrl(botName, channelName, encodedMessage))
                    .ifPresentOrElse(
                            doc -> handleTelegramResponse(doc.body().text()),
                            () -> log.error("Telegram: no response from server.")
                    );
        } catch (Exception e) {
            log.error("Telegram: could not send message: {}", e.getMessage());
        }
    }

    private void handleTelegramResponse(String response) {
        if (response.matches(".*\"ok\":true.*")) {
            log.info("Telegram: message send");
        } else {
            log.error("Telegram: message send with message: {}", response);
        }
    }

    private String createUrl(String botName, String channelName, String message) {
        return  BASE +
                BOT_BASE + botName +
                CHANNEL_BASE + channelName +
                PARSE_MODE +
                MESSAGE_BASE + message;
    }
}
