package nl.dejagermc.homefeeder.reporting.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Service
public class Telegram {

    private static final Logger LOG = LoggerFactory.getLogger(Telegram.class);

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
        String uri = createUrl(botName, channelName, message);

        try {
            URL url = new URL(uri);
            URLConnection conn = url.openConnection();

            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            String response = sb.toString();
            handleTelegramResponse(response);
        } catch (Exception e) {
            LOG.error("Error sending message to telegram: {}", e);
        }
    }

    private void handleTelegramResponse(String response) {
        if (response.matches(".*\"ok\":true.*")) {
            LOG.info("Telegram message: ok");
        } else {
            LOG.warn("Telegram message: not ok: {}", response);
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
