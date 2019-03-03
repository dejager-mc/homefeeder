package nl.dejagermc.homefeeder.web.dto.settings;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors
@Getter
@Setter
@ToString
public class HomeFeederSettingsDto {
    private boolean useGoogleHome;
    private boolean useTelegram;
}
