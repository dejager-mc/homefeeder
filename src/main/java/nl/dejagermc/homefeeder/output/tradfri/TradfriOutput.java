package nl.dejagermc.homefeeder.output.tradfri;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.CoapClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TradfriOutput {
    @Value("${tradfri.ip}")
    private String ip;

    private final String userName = "homefeeder";

    @Value("${tradfri.gateway.code}")
    private String gatewayCode;

    private String sharedKey;

    private CoapClient getClient() {
        return new CoapClient("coap://iot.eclipse.org:5683/obs");
    }

    private void setSharedKey(CoapClient client) {
        client.post("", 0);
    }
}
