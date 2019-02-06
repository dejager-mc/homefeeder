package nl.dejagermc.homefeeder.business;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutput;
import nl.dejagermc.homefeeder.output.stream.StreamOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StreamOutputService {
    private OpenhabOutput openhabOutput;
    private StreamOutput streamOutput;

    @Autowired
    public StreamOutputService(OpenhabOutput openhabOutput, StreamOutput streamOutput) {
        this.openhabOutput = openhabOutput;
        this.streamOutput = streamOutput;
    }

    public void watchStream() {
        openhabOutput.turnOnTv();
        streamOutput.openStream("");
    }
}
