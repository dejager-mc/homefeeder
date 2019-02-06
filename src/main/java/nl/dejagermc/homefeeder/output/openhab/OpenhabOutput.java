package nl.dejagermc.homefeeder.output.openhab;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class OpenhabOutput {

    private static final String OPENHAB_API_URI = "192.168.1.12:8080/classicui/CMD?woonkamer_tradfri_lampen_switch=ON";

    public void turnOnTv() {
        turnOnOpenhabItem("");
    }

    private void turnOnOpenhabItem(String item) {
        try {
            String response = Jsoup.connect(OPENHAB_API_URI)
                    .timeout(5000)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/json")
//                    .requestBody(json)
                    .execute()
                    .body();
            handleResponse(response);
        } catch (IOException e) {
            log.error("Error turning tv on:", e);
        }
    }

    private void handleResponse(String response) {
        if (response.matches(".*\"ok\":true.*")) {
            log.info("openhab: ok");
        } else {
            log.error("openhab: not ok: {}", response);
        }
    }

}
