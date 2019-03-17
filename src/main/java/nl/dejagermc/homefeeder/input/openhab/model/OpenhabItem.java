package nl.dejagermc.homefeeder.input.openhab.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@Accessors
@Getter
@Setter
@ToString
@Builder
public class OpenhabItem {
    private String link;
    private String state;
    private String label;
    private String name;
    private String type;
}
