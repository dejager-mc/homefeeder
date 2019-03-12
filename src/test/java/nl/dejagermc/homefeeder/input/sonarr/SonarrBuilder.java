package nl.dejagermc.homefeeder.input.sonarr;

import nl.dejagermc.homefeeder.domain.generated.sonarr.Episode;
import nl.dejagermc.homefeeder.domain.generated.sonarr.Series;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;

import java.util.Arrays;

public class SonarrBuilder {

    public static SonarrWebhookSchema getDefaultSonarrSchema() {
        SonarrWebhookSchema schema = new SonarrWebhookSchema();
        schema.setEpisodes(Arrays.asList(getEpisode()));
        schema.setSeries(getSeries());
        return schema;
    }

    private static Episode getEpisode() {
        Episode episode = new Episode();
        episode.setTitle("Episode title");
        episode.setSeasonNumber(1);
        episode.setEpisodeNumber(1);
        episode.setQuality("BEST");
        return episode;
    }

    private static Series getSeries() {
        Series series = new Series();
        series.setTitle("New Series");
        return series;
    }
}
