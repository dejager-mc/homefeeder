package nl.dejagermc.homefeeder.input.openhab.predicates;

import nl.dejagermc.homefeeder.input.openhab.model.OpenhabItem;

import java.util.function.Predicate;

public class OpenhabItemPredicates {

    private OpenhabItemPredicates() {
        // private
    }

    public static Predicate<OpenhabItem> isItemASwitch() {
        return item -> item.getType().equals("Switch") || item.getType().equals("Group");
    }

    public static Predicate<OpenhabItem> isItemAString() {
        return item -> item.getType().equals("String");
    }
}
