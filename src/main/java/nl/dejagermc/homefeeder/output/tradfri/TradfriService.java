package nl.dejagermc.homefeeder.output.tradfri;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.util.coap.CoapUtil;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.support.ExecutorServiceAdapter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import org.eclipse.californium.scandium.config.DtlsConnectorConfig;

import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class TradfriService {
    private static final String COAP = "coaps://";
    private static final String REBOOT = "/15011/9030";
    private static final String ALL_DEVICES = "/15001";

    @Value("${tradfri.ip}")
    private String ip;
    @Value("${tradfri.gateway.code}")
    private String gatewayCode;

    private CoapUtil coapUtil;

    @Autowired
    public TradfriService(CoapUtil coapUtil) {
        this.coapUtil = coapUtil;
    }

    public boolean rebootGateway() {
        log.info("UC200: sending reboot command");
        String uri = getBaseUri() + REBOOT;
        CoapResponse response = coapUtil.postEmptyBody(uri, gatewayCode);
        if (response != null && response.isSuccess()) {
            log.info("UC200: reboot started");
            return true;
        } else {
            if (null == response) {
                log.error("UC200: timeout sending reboot command");
                return false;
            } else {
                log.error("UC200: error sending reboot command: {}", response.getResponseText());
                return false;
            }
        }
    }

//    @Retryable(value = {TradfriException.class}, maxAttempts = 60, backoff = @Backoff(3000L))
    public boolean isGatewayUpRetryable() throws TradfriException {
        String uri = getBaseUri() + ALL_DEVICES;
        CoapResponse response = coapUtil.get(uri, gatewayCode);
        if (response == null) {
            log.info("UC202: gateway is not up yet.");
            throw new TradfriException();
        }
        return true;
    }

    public String getAllDevices() {
        String uri = getBaseUri() + ALL_DEVICES;
        CoapResponse response = coapUtil.get(uri, gatewayCode);
        if (response == null) {
            log.error("UC201: Output: timeout");
            return "";
        } else {
            log.info("UC201: Output: devices: {}", response.getResponseText());
            return response.getResponseText();
        }
    }

    private String getBaseUri() {
        return COAP + ip;
    }
}
