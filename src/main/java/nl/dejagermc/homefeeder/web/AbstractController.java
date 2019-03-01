package nl.dejagermc.homefeeder.web;

import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;

public class AbstractController {

    SettingsService settingsService;

    public AbstractController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
