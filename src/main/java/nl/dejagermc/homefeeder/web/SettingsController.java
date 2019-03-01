package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.dto.HomeFeederSettingsDto;
import nl.dejagermc.homefeeder.input.homefeeder.dto.OpenHabSettingsDto;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederSettings;
import nl.dejagermc.homefeeder.input.homefeeder.model.OpenHabSettings;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("settings")
@Slf4j
public class SettingsController extends AbstractController {

    private ModelMapper modelMapper;

    @Autowired
    public SettingsController(SettingsService settingsService, ModelMapper modelMapper) {
        super(settingsService);
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "settings/openhab", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public OpenHabSettingsDto getOpenHabSettings() {
        return convertToDto(settingsService.getOpenHabSettings());
    }

    @PutMapping(value = "settings/openhab", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateOpenhabSettings(@RequestBody final OpenHabSettingsDto openHabStateDto) {
        OpenHabSettings openHabSettings = convertToEntity(openHabStateDto);
        settingsService.getOpenHabSettings().setHome(openHabSettings.isHome());
        settingsService.getOpenHabSettings().setSleeping(openHabSettings.isSleeping());
        settingsService.getOpenHabSettings().setMute(openHabSettings.isMute());
    }

    @GetMapping(value = "settings/homefeeder", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HomeFeederSettingsDto getHomeFeederSettings() {
        return convertToDto(settingsService.getHomeFeederSettings());
    }

    @PutMapping(value = "settings/homefeeder", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateHomeFeederState(@RequestBody final HomeFeederSettingsDto homeFeederStateDto) {
        HomeFeederSettings newHomeFeederSettings = convertToEntity(homeFeederStateDto);
        settingsService.getHomeFeederSettings().setUseGoogleHome(newHomeFeederSettings.isUseGoogleHome());
        settingsService.getHomeFeederSettings().setUseTelegram(newHomeFeederSettings.isUseTelegram());
    }

    private OpenHabSettingsDto convertToDto(OpenHabSettings openHabSettings) {
        return modelMapper.map(openHabSettings, OpenHabSettingsDto.class);
    }

    private OpenHabSettings convertToEntity(OpenHabSettingsDto openHabStateDto) {
        return modelMapper.map(openHabStateDto, OpenHabSettings.class);
    }

    private HomeFeederSettingsDto convertToDto(HomeFeederSettings homeFeederSettings) {
        return modelMapper.map(homeFeederSettings, HomeFeederSettingsDto.class);
    }

    private HomeFeederSettings convertToEntity(HomeFeederSettingsDto homeFeederStateDto) {
        return modelMapper.map(homeFeederStateDto, HomeFeederSettings.class);
    }
}
