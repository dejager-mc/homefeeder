package nl.dejagermc.homefeeder.schudulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OpenhabScheduler {

    private OpenhabOutput openhabOutput;

    @Autowired
    public OpenhabScheduler(OpenhabOutput openhabOutput) {
        this.openhabOutput = openhabOutput;
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 10000)
    public void homefeederUpAndRunning() {
        openhabOutput.homefeederIsOnline();
    }
}
