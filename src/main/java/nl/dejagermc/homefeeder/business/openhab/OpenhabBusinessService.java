package nl.dejagermc.homefeeder.business.openhab;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.openhab.OpenhabInputService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OpenhabBusinessService extends AbstractBusinessService {

    private OpenhabInputService openhabInputService;
    private OpenhabOutputService openhabOutputService;
    private CacheManager cacheManager;

    @Autowired
    public OpenhabBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService, OpenhabInputService openhabInputService, OpenhabOutputService openhabOutputService, CacheManager cacheManager) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
        this.openhabInputService = openhabInputService;
        this.openhabOutputService = openhabOutputService;
        this.cacheManager = cacheManager;
    }

    public void refreshItems() {
        cacheManager.getCache("getAllOpenhabItems").clear();
        openhabInputService.getAllOpenhabItems();
    }
}
