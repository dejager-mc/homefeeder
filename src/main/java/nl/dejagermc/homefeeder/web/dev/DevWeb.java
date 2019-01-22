package nl.dejagermc.homefeeder.web.dev;

import nl.dejagermc.homefeeder.reporting.telegram.Telegram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

@RestController
public class DevWeb {

    private Telegram telegram;

    @Autowired
    public DevWeb(Telegram telegram) {
        Assert.notNull(telegram, "telegram must not be null");
        this.telegram = telegram;
    }

    @GetMapping("/telegram")
    public String greeting(@RequestParam(value="text") String text) {
        telegram.sendMessage(text);
        return "Sending " + text;
    }
}
