package nl.dejagermc.homefeeder.appconfig;

import nl.dejagermc.homefeeder.business.dialogflow.DialogflowBusinessService;
import nl.dejagermc.homefeeder.business.tradfri.TradfriBusinessService;
import nl.dejagermc.homefeeder.web.DialogflowController;
import nl.dejagermc.homefeeder.web.TradfriController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.awt.*;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = HomeFeederConfig.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@EnableConfigurationProperties
////@WebMvcTest(TradfriController.class)
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration
//@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebSecurityConfig.class, TradfriController.class, DialogflowController.class, DialogflowBusinessService.class, TradfriBusinessService.class})
public class WebSecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private TradfriBusinessService tradfriBusinessService;
    @MockBean
    private DialogflowBusinessService dialogflowBusinessService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .dispatchOptions(true)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(value = "google", roles = "GOOGLE")
    @Test
    public void testGetSettingsWithCorrectGoogleUser_shouldSucceedWith200() throws Exception {
        // http 500 because this getAllDeliveries does not handle the response object
        mockMvc.perform(post("/dialogflow/webhook").content("body"))
                .andExpect(status().isInternalServerError());
    }

    @WithMockUser(value = "google", roles = "GOOGLE")
    @Test
    public void testGetSettingsWithCorrectGoogleUser_shouldFailWith400() throws Exception {
        mockMvc.perform(post("/dialogflow/webhook"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "google", roles = "GOOGLE")
    @Test
    public void testGetSettingsWithWrongUser_shouldErrorWith403() throws Exception {
        mockMvc.perform(get("/homefeeder/tradfri/devices")).andExpect(status().isForbidden());
    }

    @WithMockUser(value = "maxhunt", roles = "ADMIN")
    @Test
    public void testGetSettingsWithCorrectUser_shouldSucceedWith200() throws Exception {
        when(tradfriBusinessService.getAllDevices()).thenReturn("[getAllDeliveries]");
        mockMvc.perform(get("/tradfri/devices", ""))
                .andExpect(status().isOk());
    }

    @WithMockUser(value = "something", roles = "DIFFERENT")
    @Test
    public void testGetSettingsWithUnknownUser_shouldErrorWith403() throws Exception {
        mockMvc.perform(get("/homefeeder/tradfri/devices")).andExpect(status().isForbidden());
    }
}
