package nl.dejagermc.homefeeder.input.openhab.model;

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
public class OpenhabThing {
    private String link;
    private String state;
    private String label;
    private String name;
    private String type;
}
