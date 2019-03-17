package nl.dejagermc.homefeeder.output.tradfri;

import nl.dejagermc.homefeeder.util.coap.CoapUtil;
import org.eclipse.californium.core.CoapResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradfriService.class)
@EnableConfigurationProperties
public class TradfriServiceTest {

    @Autowired
    private TradfriService tradfriService;

    @MockBean
    private CoapUtil coapUtil;
    @MockBean
    private CoapResponse coapResponse;

    @Test
    public void testRebootSuccess() {
        when(coapUtil.postEmptyBody(anyString(), anyString())).thenReturn(coapResponse);
        when(coapResponse.isSuccess()).thenReturn(true);
        boolean response = tradfriService.rebootGateway();
        validateMockitoUsage();
        assertTrue(response);
    }

    @Test
    public void testRebootFailure() {
        when(coapUtil.postEmptyBody(anyString(), anyString())).thenReturn(coapResponse);
        when(coapResponse.isSuccess()).thenReturn(false);
        boolean response = tradfriService.rebootGateway();
        validateMockitoUsage();
        assertFalse(response);
    }

    @Test
    public void testGetAllDevicesSuccess() {
        String responseText = "{1234}";
        when(coapUtil.get(anyString(), anyString())).thenReturn(coapResponse);
        when(coapResponse.getResponseText()).thenReturn(responseText);
        String response = tradfriService.getAllDevices();
        validateMockitoUsage();
        assertEquals(response, responseText);
    }

    @Test
    public void testGetAllDevicesFailure() {
        when(coapUtil.get(anyString(), anyString())).thenReturn(null);
        String response = tradfriService.getAllDevices();
        validateMockitoUsage();
        assertEquals(response, "");
    }

    @Test
    public void testIsGatewayUpSuccess() throws TradfriException {
        when(coapUtil.get(anyString(), anyString())).thenReturn(coapResponse);
        validateMockitoUsage();
        boolean response = tradfriService.isGatewayUpRetryable();
        assertTrue(response);
    }

    @Test(expected = TradfriException.class)
    public void testIsGatewayUpFailure() throws TradfriException {
        when(coapUtil.get(anyString(), anyString())).thenReturn(null);
        validateMockitoUsage();
        tradfriService.isGatewayUpRetryable();
    }
}