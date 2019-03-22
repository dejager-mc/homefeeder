package nl.dejagermc.homefeeder.input.postnl.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
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

    public String getFormattedDeliveryTime() {
        final String deliveryTime = "%s between %s and %s";
        final String dateFormatter = "d MMM yyyy";
        final String timeFormatter = "h:mm";
        if (startTime != null) {
            LocalDate day = startTime.toLocalDate();

        }
        return "unknown delivery time.";
    }

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
