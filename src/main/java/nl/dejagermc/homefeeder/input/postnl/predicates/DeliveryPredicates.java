package nl.dejagermc.homefeeder.input.postnl.predicates;

import nl.dejagermc.homefeeder.input.postnl.model.Delivery;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class DeliveryPredicates {

    private DeliveryPredicates() {
        // private
    }

    public static Predicate<Delivery> isDeliveryForToday() {
        return delivery -> delivery.startTime().isAfter(LocalDateTime.now().toLocalDate().atStartOfDay()) &&
                delivery.endTime().isBefore(LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
    }
}
