package nl.dejagermc.homefeeder.input.groningen.rubbish.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import nl.dejagermc.homefeeder.input.groningen.rubbish.enums.BinType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Accessors
@Getter
@Setter
@ToString
public class BinPickup {
    private LocalDate pickupDay;
    private BinType binType;
}
