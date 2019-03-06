package nl.dejagermc.homefeeder.input.homefeeder.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Accessors
@Getter
@Setter
@ToString
public class HomeFeederSettings {
    Set<ReportMethods> reportMethods = Set.of(ReportMethods.GOOGLE_HOME, ReportMethods.TELEGRAM);
    // nothing yet
}
