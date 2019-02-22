package nl.dejagermc.homefeeder.startup;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class StartupCheck {

    private OpenhabOutput openhabOutput;

    @Autowired
    public StartupCheck(OpenhabOutput openhabOutput) {
        this.openhabOutput = openhabOutput;
    }

    @PostConstruct
    public void startupChecks() {
        checkIfOpenhabIsUp();
    }

    private void checkIfOpenhabIsUp() {
        boolean success = openhabOutput.homefeederIsOnline();
        if (success) {
            log.info("### Openhab: Connected");
        } else {
            log.info("### Openhab: Not Connected");
        }
    }
}
