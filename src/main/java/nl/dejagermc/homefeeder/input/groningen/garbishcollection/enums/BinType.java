package nl.dejagermc.homefeeder.input.groningen.garbishcollection.enums;

public enum BinType {
    GRAY("Grijze container"),
    GREEN("Groene container"),
    BLUE("Oud papier");

    private String name;

    BinType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
