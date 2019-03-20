package nl.dejagermc.homefeeder.input.homefeeder.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Accessors
@Getter
@Setter
@ToString
public class HomeFeederSettings {
    Set<ReportMethod> reportMethods = Set.of(ReportMethod.GOOGLE_HOME, ReportMethod.TELEGRAM);
}
