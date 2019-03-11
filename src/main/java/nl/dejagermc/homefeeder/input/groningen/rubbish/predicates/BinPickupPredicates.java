package nl.dejagermc.homefeeder.input.groningen.rubbish.predicates;

import nl.dejagermc.homefeeder.input.groningen.rubbish.model.BinPickup;

import java.util.Comparator;

public class BinPickupPredicates {

    private BinPickupPredicates() {
        // private
    }

    public static Comparator<BinPickup> sortBinPickups() {
        return Comparator
                .comparing(BinPickup::getPickupDay);
    }
}
