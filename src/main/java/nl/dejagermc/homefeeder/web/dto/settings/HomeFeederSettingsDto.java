package nl.dejagermc.homefeeder.web.dto.settings;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod;

import java.util.Set;

@Accessors
@Getter
@Setter
@ToString
public class HomeFeederSettingsDto {
    private Set<ReportMethod> reportMethods;
}
