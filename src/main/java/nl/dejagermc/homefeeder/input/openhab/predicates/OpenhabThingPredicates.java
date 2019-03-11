package nl.dejagermc.homefeeder.input.openhab.predicates;

import nl.dejagermc.homefeeder.input.openhab.model.OpenhabThing;

import java.util.function.Predicate;

public class OpenhabThingPredicates {

    private OpenhabThingPredicates() {
        // private
    }

    public static Predicate<OpenhabThing> isThingASwitch() {
        return thing -> thing.getType().equals("Switch") || thing.getType().equals("Group");
    }

    public static Predicate<OpenhabThing> isThingAString() {
        return thing -> thing.getType().equals("String");
    }
}
