package nl.dejagermc.homefeeder.output.tradfri;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.CoapClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.eclipse.californium.scandium.config.DtlsConnectorConfig;

import java.net.InetSocketAddress;

@Service
@Slf4j
public class TradfriOutput {
    private static final String COAP = "coaps://";
    private static final String REBOOT = "/15011/9030";

    @Value("${tradfri.ip}")
    private String ip;
    private String port = ":5684";

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

    public void test() {
        DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
//        builder.setPskStore()
    }
}
