package nl.dejagermc.homefeeder.input.homefeeder.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@Component
@Accessors
@Getter
@Setter
@ToString
public class OpenHabSettings {
    private boolean isHomeMuted;
}