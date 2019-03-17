package nl.dejagermc.homefeeder.output.openhab;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.openhab.OpenhabInputService;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static nl.dejagermc.homefeeder.input.openhab.builders.OpenhabItemBuilders.switchItem;
import static nl.dejagermc.homefeeder.input.openhab.builders.OpenhabItemBuilders.tvItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OpenhabOutputService.class})
@EnableConfigurationProperties
public class OpenhabOutputServiceTest {

    @Autowired
    private OpenhabOutputService openhabOutputService;
    @MockBean
    private HttpUtil httpUtil;
    @MockBean
    private OpenhabInputService openhabInputService;

    @Test
    public void testActionOnStringItemGood() {
        when(httpUtil.postJsonToOpenhab(anyString(), anyString())).thenReturn("");
        boolean result = openhabOutputService.performActionOnStringItem("ON", tvItem("1"));
        validateMockitoUsage();
        assertTrue(result);
    }

    @Test
    public void testActionOnStringItemBadResponse() {
        when(httpUtil.postJsonToOpenhab(anyString(), anyString())).thenReturn("404");
        boolean result = openhabOutputService.performActionOnStringItem("ON", tvItem("1"));
        validateMockitoUsage();
        assertFalse(result);
    }

    @Test
    public void testActionOnSwitchItemGood() {
        when(httpUtil.postJsonToOpenhab(anyString(), anyString())).thenReturn("");
        boolean result = openhabOutputService.performActionOnSwitchItem("ON", tvItem("1"));
        validateMockitoUsage();
        assertTrue(result);
    }

    @Test
    public void testActionOnSwitchItemBadResponse() {
        when(httpUtil.postJsonToOpenhab(anyString(), anyString())).thenReturn("404");
        boolean result = openhabOutputService.performActionOnSwitchItem("ON", tvItem("1"));
        validateMockitoUsage();
        assertFalse(result);
    }

    @Test
    public void testHomeFeederIsOnlineNoResponse() {
        when(openhabInputService.findOpenhabItemWithName(anyString())).thenReturn(Optional.empty());
        boolean result = openhabOutputService.homefeederIsOnline();
        validateMockitoUsage();
        assertFalse(result);
    }

    @Test
    public void testHomeFeederIsOnlineGoodResponse() {
        when(openhabInputService.findOpenhabItemWithName(anyString())).thenReturn(Optional.of(switchItem()));
        when(httpUtil.postJsonToOpenhab(anyString(), anyString())).thenReturn("");
        boolean result = openhabOutputService.homefeederIsOnline();
        validateMockitoUsage();
        assertTrue(result);
    }
}
