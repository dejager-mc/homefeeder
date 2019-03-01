package nl.dejagermc.homefeeder.input.homefeeder.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors
@Getter
@Setter
@ToString
public class OpenHabSettingsDto {
    private boolean isHome;
    private boolean isSleeping;
    private boolean isMute;
}