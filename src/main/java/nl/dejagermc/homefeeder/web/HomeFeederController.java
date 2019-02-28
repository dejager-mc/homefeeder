package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.dto.HomeFeederStateDto;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;
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

    @Autowired
    public HomeFeederController(ModelMapper modelMapper, HomeFeederState homeFeederState) {
        this.modelMapper = modelMapper;
        this.homeFeederState = homeFeederState;
    }

    @GetMapping(value = "state", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HomeFeederStateDto getHomeFeederState() {
        return convertToDto(homeFeederState);
    }

    @PutMapping(value = "state/openhab", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateHomeFeederStateOpenhabSettings(@RequestBody final HomeFeederStateDto homeFeederStateDto) {
        HomeFeederState newHomeFeederState = convertToEntity(homeFeederStateDto);
        homeFeederState.isHome(newHomeFeederState.isHome());
        homeFeederState.isSleeping(newHomeFeederState.isSleeping());
        homeFeederState.isMute(newHomeFeederState.isMute());
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

    private HomeFeederStateDto convertToDto(HomeFeederState homeFeederState) {
        return modelMapper.map(homeFeederState, HomeFeederStateDto.class);
    }

    private HomeFeederState convertToEntity(HomeFeederStateDto homeFeederStateDto) {
        return modelMapper.map(homeFeederStateDto, HomeFeederState.class);
    }
}
