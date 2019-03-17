package nl.dejagermc.homefeeder.util.coap;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CoapUtil {

    public CoapUtil() {
        // empty
    }

    public CoapResponse postEmptyBody(final String uri, final String gatewayCode) {
        CoapEndpoint endpoint = initCoap(gatewayCode);
        CoapClient client = getCoapClient(endpoint, uri);

        Request request = new Request(CoAP.Code.POST);
        CoapResponse response = client.advanced(request);

        endpoint.destroy();
        return response;
    }

    public CoapResponse get(final String uri, final String gatewayCode) {
        CoapEndpoint endpoint = initCoap(gatewayCode);
        CoapClient client = getCoapClient(endpoint, uri);

        CoapResponse response = client.get(1);

        endpoint.destroy();
        return response;
    }

//    @SuppressWarnings("squid:CallToDeprecatedMethod")
    private CoapEndpoint initCoap(final String gatewayCode) {
        DtlsConnectorConfig connectorConfig = new DtlsConnectorConfig.Builder()
                .setPskStore(new StaticPskStore("", gatewayCode.getBytes()))
                .build();
//        builder.setPskStore(new StaticPskStore("", gatewayCode.getBytes()));

        return new CoapEndpoint.CoapEndpointBuilder()
                .setConnector(new DTLSConnector(connectorConfig))
                .setNetworkConfig(NetworkConfig.getStandard())
                .build();


//        return new CoapEndpoint(new DTLSConnector(builder.build()), NetworkConfig.getStandard());
    }

    private CoapClient getCoapClient(CoapEndpoint endpoint, String uri) {
        CoapClient client = new CoapClient(uri);
        client.setEndpoint(endpoint);
        client.setTimeout(2000L);
        return client;
    }
}
