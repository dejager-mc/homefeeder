package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.domain.generated.radarr.RemoteMovie;
import nl.dejagermc.homefeeder.domain.generated.sonarr.Episode;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.radarr.RadarrService;
import nl.dejagermc.homefeeder.input.sonarr.SonarrService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class DownloadReportBusinessService extends AbstractReportBusinessService {

    private static final String TELEGRAM_MOVIE_REPORT = "<b>Movie available</b>%n%s (%s)%n";
    private static final String GOOGLE_HOME_MOVIE_REPORT = "The movie %s is now available.";

    private static final String TELEGRAM_SERIES_REPORT = "<b>Episode available</b>%n%s - %sx%s - %s [%s]";
    private static final String GOOGLE_HOME_SERIES_ONE_EPISODE_REPORT = "%s, episode %s now available.";
    private static final String GOOGLE_HOME_SERIES_MULTIPLE_EPISODES_REPORT = "%s episodes for series %s are now available.";

    private static final String SUMMARY_MESSAGE_TEMPLATE = "%s new %s now available. ";

    private RadarrService radarrService;
    private SonarrService sonarrService;

    @Autowired
    public DownloadReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput, RadarrService radarrService, SonarrService sonarrService) {
        super(settingsService, reportedBusinessService, telegramOutput, googleHomeOutput);
        this.radarrService = radarrService;
        this.sonarrService = sonarrService;
    }

    public void reportRadarr(RadarrWebhookSchema schema) {
        RemoteMovie remoteMovie = schema.getRemoteMovie();

        // telegram
        String telegramReport = String.format(TELEGRAM_MOVIE_REPORT,
                remoteMovie.getTitle(),
                remoteMovie.getYear());

        telegramOutput.sendMessage(telegramReport);

        // google home
        reportRadarrToGoogleHome(schema);
    }

    private void reportRadarrToGoogleHome(RadarrWebhookSchema schema) {
        if (settingsService.surpressMessage()) {
            return;
        }

        RemoteMovie remoteMovie = schema.getRemoteMovie();

        if (settingsService.saveOutputForLater()) {
            log.info("Save for later: {}", schema.getRemoteMovie());
            radarrService.addNotYetReported(schema);
        } else {
            String googleHomeReport = String.format(GOOGLE_HOME_MOVIE_REPORT,
                    remoteMovie.getTitle());

            googleHomeOutput.broadcast(googleHomeReport);
        }
    }

    public void reportSonarr(SonarrWebhookSchema schema) {
        String seriesName = schema.getSeries().getTitle();

        // telegram
        String telegramReport;
        for (Episode episode : schema.getEpisodes()) {
            telegramReport = String.format(TELEGRAM_SERIES_REPORT,
                    seriesName,
                    episode.getSeasonNumber(),
                    episode.getEpisodeNumber(),
                    episode.getTitle(),
                    episode.getQuality());
            telegramOutput.sendMessage(telegramReport);
        }

        // google home
        reportSonarrToGoogleHome(schema);
    }

    private void reportSonarrToGoogleHome(SonarrWebhookSchema schema) {
        if (settingsService.surpressMessage()) {
            return;
        }

        if (settingsService.saveOutputForLater()) {
            log.info("Save for later: {}", schema);
            sonarrService.addNotYetReported(schema);
        } else {
            String seriesName = schema.getSeries().getTitle();
            if (schema.getEpisodes().size() > 1) {
                String googleHomeReport = String.format(GOOGLE_HOME_SERIES_MULTIPLE_EPISODES_REPORT,
                        schema.getEpisodes().size(),
                        seriesName);
                googleHomeOutput.broadcast(googleHomeReport);
            } else if (schema.getEpisodes().size() == 1) {
                String googleHomeReport = String.format(GOOGLE_HOME_SERIES_ONE_EPISODE_REPORT,
                        seriesName,
                        schema.getEpisodes().get(0).getEpisodeNumber());
                googleHomeOutput.broadcast(googleHomeReport);
            }
        }
    }

    public void reportSummary() {
        StringBuilder sb = new StringBuilder();
        addSonarrToSummary(sb);
        addRadarrToSummary(sb);
        if (sb.length() > 0) {
            googleHomeOutput.broadcast(sb.toString());
        }
        sonarrService.resetNotYetReported();
        radarrService.resetNotYetReported();
    }

    private void addSonarrToSummary(StringBuilder sb) {
        Set<SonarrWebhookSchema> schemas = sonarrService.getNotYetReported();

        if (schemas.size() == 1) {
            sb.append(String.format(SUMMARY_MESSAGE_TEMPLATE, 1, "epsiode is"));
        }
        if (schemas.size() > 1) {
            sb.append(String.format(SUMMARY_MESSAGE_TEMPLATE, schemas.size(), "epsiodes are"));
        }
    }

    private void addRadarrToSummary(StringBuilder sb) {
        Set<RadarrWebhookSchema> schemas = radarrService.getNotYetReported();

        if (schemas.size() == 1) {
            sb.append(String.format(SUMMARY_MESSAGE_TEMPLATE, 1, "movie is"));
        }
        if (schemas.size() > 1) {
            sb.append(String.format(SUMMARY_MESSAGE_TEMPLATE, schemas.size(), "movies are"));
        }
    }
}
