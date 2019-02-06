package nl.dejagermc.homefeeder.output.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class StreamOutput {

    public void openStream(String uri) {
        String psexec = "";
        String user = "";
        String pass = "";
        String streamlink = "streamlink";
        String pc = "";

        String params = String.format("\\\\%s -u %s -p %s -h -i -d %s %s",
                psexec,
                pc,
                user,
                pass,
                streamlink,
                uri);

        try {
            Process process = new ProcessBuilder(psexec, params).start();
        } catch (IOException e) {
            log.error("Error opening stream.", e);
        }
    }
}
