package nl.dejagermc.homefeeder.input.liquipedia.dota.model;

public enum TournamentType {
    PREMIER("Premier"),
    MAJOR("Major"),
    QUALIFIER("Qualifier");

    private String name;

    TournamentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
