package nl.dejagermc.homefeeder.input.groningen.garbishcollection.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import nl.dejagermc.homefeeder.input.groningen.garbishcollection.enums.BinType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Accessors
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BinPickup {
    private LocalDate pickupDay;
    private BinType binType;
}
