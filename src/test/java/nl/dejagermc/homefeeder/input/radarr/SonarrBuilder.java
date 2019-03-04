package nl.dejagermc.homefeeder.input.radarr;

import nl.dejagermc.homefeeder.domain.generated.sonarr.Episode;
import nl.dejagermc.homefeeder.domain.generated.sonarr.Series;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods;

import java.util.Arrays;
import java.util.Set;

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

//    String seriesName = schema.getSeries().getTitle();
//    Set<ReportMethods> reportMethods = settingsService.getReportMethods();
//
//        if (reportMethods.contains(ReportMethods.TELEGRAM)) {
//        String telegramReport;
//        for (Episode episode : schema.getEpisodes()) {
//            telegramReport = String.format(TELEGRAM_SERIES_REPORT,
//                    seriesName,
//                    episode.getSeasonNumber(),
//                    episode.getEpisodeNumber(),
//                    episode.getTitle(),
//                    episode.getQuality());
//            telegramOutput.sendMessage(telegramReport);
//        }
//    }
}
