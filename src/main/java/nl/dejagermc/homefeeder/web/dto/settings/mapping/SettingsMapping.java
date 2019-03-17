package nl.dejagermc.homefeeder.web.dto.settings.mapping;

import nl.dejagermc.homefeeder.input.homefeeder.model.DotaSettings;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederSettings;
import nl.dejagermc.homefeeder.input.homefeeder.model.OpenHabSettings;
import nl.dejagermc.homefeeder.web.dto.settings.AllSettingsDto;
import nl.dejagermc.homefeeder.web.dto.settings.DotaSettingsDto;
import nl.dejagermc.homefeeder.web.dto.settings.HomeFeederSettingsDto;
import nl.dejagermc.homefeeder.web.dto.settings.OpenHabSettingsDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsMapping {
    private ModelMapper modelMapper;

    @Autowired
    public SettingsMapping(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public OpenHabSettingsDto convertToDto(OpenHabSettings openHabSettings) {
        return modelMapper.map(openHabSettings, OpenHabSettingsDto.class);
    }

    public OpenHabSettings convertToEntity(OpenHabSettingsDto openHabStateDto) {
        return modelMapper.map(openHabStateDto, OpenHabSettings.class);
    }

    public HomeFeederSettingsDto convertToDto(HomeFeederSettings homeFeederSettings) {
        return modelMapper.map(homeFeederSettings, HomeFeederSettingsDto.class);
    }

    public HomeFeederSettings convertToEntity(HomeFeederSettingsDto homeFeederStateDto) {
        return modelMapper.map(homeFeederStateDto, HomeFeederSettings.class);
    }

    public DotaSettingsDto convertToDto(DotaSettings dotaSettings) {
        return modelMapper.map(dotaSettings, DotaSettingsDto.class);
    }

    public DotaSettings convertToEntity(DotaSettingsDto dotaSettingsDto) {
        return modelMapper.map(dotaSettingsDto, DotaSettings.class);
    }

    // combined mapping
    public AllSettingsDto convertToDto(DotaSettings dotaSettings, OpenHabSettings openHabSettings, HomeFeederSettings homeFeederSettings) {
        DotaSettingsDto dotaSettingsDto = convertToDto(dotaSettings);
        OpenHabSettingsDto openHabSettingsDto = convertToDto(openHabSettings);
        HomeFeederSettingsDto homeFeederSettingsDto = convertToDto(homeFeederSettings);

        AllSettingsDto allSettingsDto = new AllSettingsDto();
        allSettingsDto.setDotaSettings(dotaSettingsDto);
        allSettingsDto.setHomeFeederSettings(homeFeederSettingsDto);
        allSettingsDto.setOpenHabSettings(openHabSettingsDto);

        return allSettingsDto;
    }
}
