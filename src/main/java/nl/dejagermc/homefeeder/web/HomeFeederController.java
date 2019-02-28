package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.dto.HomeFeederStateDto;
import nl.dejagermc.homefeeder.input.homefeeder.dto.OpenHabStateDto;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;
import nl.dejagermc.homefeeder.input.homefeeder.model.OpenHabSettings;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("homefeeder")
@Slf4j
public class HomeFeederController {

    private ModelMapper modelMapper;
    private HomeFeederState homeFeederState;
    private OpenHabSettings openHabSettings;

    @Autowired
    public HomeFeederController(ModelMapper modelMapper, HomeFeederState homeFeederState, OpenHabSettings openHabSettings) {
        this.modelMapper = modelMapper;
        this.homeFeederState = homeFeederState;
        this.openHabSettings = openHabSettings;
    }

    @GetMapping(value = "state", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HomeFeederStateDto getHomeFeederState() {
        return convertToDto(homeFeederState);
    }

    @GetMapping(value = "state/openhab", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public OpenHabStateDto getOpenHabSettings() {
        return convertToDto(openHabSettings);
    }

    @PutMapping(value = "state/openhab", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateHomeFeederStateOpenhabSettings(@RequestBody final OpenHabStateDto openHabStateDto) {
        OpenHabSettings openHabSettings = convertToEntity(openHabStateDto);
        log.info("received openhab config: {}", openHabSettings.toString());
        homeFeederState.isHome(openHabSettings.isHome());
        homeFeederState.isSleeping(openHabSettings.isSleeping());
        homeFeederState.isMute(openHabSettings.isMute());
    }

    @PutMapping(value = "state", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateHomeFeederState(@RequestBody final HomeFeederStateDto homeFeederStateDto) {
        HomeFeederState newHomeFeederState = convertToEntity(homeFeederStateDto);
        homeFeederState.isHome(newHomeFeederState.isHome());
        homeFeederState.isSleeping(newHomeFeederState.isSleeping());
        homeFeederState.isMute(newHomeFeederState.isMute());
        homeFeederState.useGoogleHome(newHomeFeederState.useGoogleHome());
        homeFeederState.useTelegram(newHomeFeederState.useTelegram());
        homeFeederState.favoriteTeams(newHomeFeederState.favoriteTeams());
    }

    private OpenHabStateDto convertToDto(OpenHabSettings openHabSettings) {
        return modelMapper.map(openHabSettings, OpenHabStateDto.class);
    }

    private OpenHabSettings convertToEntity(OpenHabStateDto openHabStateDto) {
        return modelMapper.map(openHabStateDto, OpenHabSettings.class);
    }

    private HomeFeederStateDto convertToDto(HomeFeederState homeFeederState) {
        return modelMapper.map(homeFeederState, HomeFeederStateDto.class);
    }

    private HomeFeederState convertToEntity(HomeFeederStateDto homeFeederStateDto) {
        return modelMapper.map(homeFeederStateDto, HomeFeederState.class);
    }
}
