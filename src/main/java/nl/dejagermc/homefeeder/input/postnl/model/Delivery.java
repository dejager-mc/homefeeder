package nl.dejagermc.homefeeder.input.postnl.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Objects;

@Accessors(fluent = true)
@Getter
@Builder
@ToString
public class Delivery {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String sender;
    private int weightInGrams;
    private boolean hasBeenDelivered;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Delivery)) return false;
        Delivery delivery = (Delivery) o;
        return weightInGrams == delivery.weightInGrams &&
                Objects.equals(startTime, delivery.startTime) &&
                Objects.equals(endTime, delivery.endTime) &&
                Objects.equals(sender, delivery.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, sender, weightInGrams);
    }
}
