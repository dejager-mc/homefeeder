package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.SummaryReportBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.model.DotaSettings;
import nl.dejagermc.homefeeder.input.homefeeder.model.OpenHabSettings;
import nl.dejagermc.homefeeder.web.dto.settings.AllSettingsDto;
import nl.dejagermc.homefeeder.web.dto.settings.DotaSettingsDto;
import nl.dejagermc.homefeeder.web.dto.settings.HomeFeederSettingsDto;
import nl.dejagermc.homefeeder.web.dto.settings.OpenHabSettingsDto;
import nl.dejagermc.homefeeder.web.dto.settings.mapping.SettingsMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("settings")
@Slf4j
public class SettingsController extends AbstractController {

    private SettingsMapping settingsMapping;
    private SummaryReportBusinessService summaryReportBusinessService;

    @Autowired
    public SettingsController(SettingsService settingsService, SettingsMapping settingsMapping, SummaryReportBusinessService summaryReportBusinessService) {
        super(settingsService);
        this.settingsMapping = settingsMapping;
        this.summaryReportBusinessService = summaryReportBusinessService;
    }

    @GetMapping(value = "openhab", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public OpenHabSettingsDto getOpenHabSettings() {
        return settingsMapping.convertToDto(settingsService.getOpenHabSettings());
    }

    @PutMapping(value = "openhab", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateOpenhabSettings(@RequestBody final OpenHabSettingsDto openHabStateDto) {
        log.info("UC010: update openhab settings to {}.", openHabStateDto);
        boolean wasHomeMuted = settingsService.getOpenHabSettings().isHomeMuted();
        OpenHabSettings openHabSettings = settingsMapping.convertToEntity(openHabStateDto);
        settingsService.getOpenHabSettings().setHomeMuted(openHabSettings.isHomeMuted());

        if (wasHomeMuted && !settingsService.getOpenHabSettings().isHomeMuted()) {
            log.info("UC003: user is now listening, report saved messages.");
            summaryReportBusinessService.reportSummary();
        }
    }

    @GetMapping(value = "homefeeder", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HomeFeederSettingsDto getHomeFeederSettings() {
        return settingsMapping.convertToDto(settingsService.getHomeFeederSettings());
    }

    @GetMapping(value = "dota", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DotaSettingsDto getDotaSettings() {
        return settingsMapping.convertToDto(settingsService.getDotaSettings());
    }

    @PutMapping(value = "dota", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateDotaState(@RequestBody final DotaSettingsDto dotaSettingsDto) {
        log.info("UC011: update dota settings.");
        DotaSettings newDotaSettings = settingsMapping.convertToEntity(dotaSettingsDto);
        settingsService.getDotaSettings().setFavoriteTeams(newDotaSettings.getFavoriteTeams());
    }

    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public AllSettingsDto getAllSettings() {
        return settingsMapping.convertToDto(settingsService.getDotaSettings(), settingsService.getOpenHabSettings(), settingsService.getHomeFeederSettings());
    }
}
