package nl.dejagermc.homefeeder.web.dto.settings;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors
@Getter
@Setter
@ToString
public class DotaSettingsDto {
    private List<String> favoriteTeams;
}
