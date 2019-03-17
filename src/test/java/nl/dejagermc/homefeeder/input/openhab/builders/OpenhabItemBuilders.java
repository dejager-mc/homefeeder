package nl.dejagermc.homefeeder.input.openhab.builders;

import nl.dejagermc.homefeeder.input.openhab.model.OpenhabItem;

public class OpenhabItemBuilders {

    public static OpenhabItem tvItem(String id) {
        return OpenhabItem.builder()
                .label("sony tv " + id)
                .link("sony.tv.link")
                .type("Switch")
                .name("tv_name" + id)
                .build();
    }

    public static OpenhabItem tvStreamItem(String id) {
        return OpenhabItem.builder()
                .label("sony tv " + id + " stream")
                .link("sony.tv.link")
                .type("Switch")
                .name("stream_name" + id)
                .build();
    }

    public static OpenhabItem switchItem() {
        return OpenhabItem.builder()
                .label("switch item")
                .link("switch.link")
                .type("Switch")
                .name("switch_name")
                .build();
    }

    public static OpenhabItem stringItem() {
        return OpenhabItem.builder()
                .label("string item")
                .link("string.link")
                .type("String")
                .name("string_name")
                .build();
    }

    public static OpenhabItem kitchenLightsItem() {
        return OpenhabItem.builder()
                .label("kitchen lights")
                .link("kitchen.lights.link")
                .type("Switch")
                .name("kitchen_light_switch")
                .build();
    }
}
