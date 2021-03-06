package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.domain.generated.radarr.RemoteMovie;
import nl.dejagermc.homefeeder.domain.generated.sonarr.Episode;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.radarr.RadarrService;
import nl.dejagermc.homefeeder.input.sonarr.SonarrService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Set;

@Service
@Slf4j
public class DownloadReportBusinessService extends AbstractBusinessService {

    private static final String TELEGRAM_MOVIE_REPORT = "<b>Movie available</b>%n%s (%s)%n";
    private static final String GOOGLE_HOME_MOVIE_REPORT = "The movie %s is now available.";

    private static final String TELEGRAM_SERIES_REPORT = "<b>Episode available</b>%n%s - %sx%s - %s [%s]";
    private static final String GOOGLE_HOME_SERIES_ONE_EPISODE_REPORT = "%s, episode %s now available.";
    private static final String GOOGLE_HOME_SERIES_MULTIPLE_EPISODES_REPORT = "%s episodes for series %s are now available.";

    private static final String SUMMARY_MESSAGE_TEMPLATE = "%s new %s now available. ";

    private RadarrService radarrService;
    private SonarrService sonarrService;

    @Inject
    public DownloadReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService, RadarrService radarrService, SonarrService sonarrService) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
        this.radarrService = radarrService;
        this.sonarrService = sonarrService;
    }

    public void reportRadarr(RadarrWebhookSchema schema) {
        RemoteMovie remoteMovie = schema.getRemoteMovie();
        log.info("UC302: reporting movie downloaded: {}", remoteMovie.getTitle());

        // telegram
        String telegramReport = String.format(TELEGRAM_MOVIE_REPORT,
                remoteMovie.getTitle(),
                remoteMovie.getYear());

        telegramOutputService.sendMessage(telegramReport);

        // google home
        reportRadarrToGoogleHome(schema);
    }

    private void reportRadarrToGoogleHome(RadarrWebhookSchema schema) {
        RemoteMovie remoteMovie = schema.getRemoteMovie();

        if (settingsService.isHomeMuted()) {
            log.info("UC302: Home muted, saving movie voice report.");
            radarrService.addNotYetReported(schema);
        } else {
            String googleHomeReport = String.format(GOOGLE_HOME_MOVIE_REPORT,
                    remoteMovie.getTitle());
            googleHomeOutputService.broadcast(googleHomeReport);
        }
    }

    public void reportSonarr(SonarrWebhookSchema schema) {
        String seriesName = schema.getSeries().getTitle();
        log.info("UC301: reporting series downloaded: {}", seriesName);

        // telegram
        String telegramReport;
        for (Episode episode : schema.getEpisodes()) {
            telegramReport = String.format(TELEGRAM_SERIES_REPORT,
                    seriesName,
                    episode.getSeasonNumber(),
                    episode.getEpisodeNumber(),
                    episode.getTitle(),
                    episode.getQuality());
            telegramOutputService.sendMessage(telegramReport);
        }

        // google home
        reportSonarrToGoogleHome(schema);
    }

    private void reportSonarrToGoogleHome(SonarrWebhookSchema schema) {
        if (settingsService.isHomeMuted()) {
            log.info("UC301: Home is muted, saving series voice report.");
            sonarrService.addNotYetReported(schema);
        } else {
            String seriesName = schema.getSeries().getTitle();
            if (schema.getEpisodes().size() > 1) {
                String googleHomeReport = String.format(GOOGLE_HOME_SERIES_MULTIPLE_EPISODES_REPORT,
                        schema.getEpisodes().size(),
                        seriesName);
                googleHomeOutputService.broadcast(googleHomeReport);
            } else if (schema.getEpisodes().size() == 1) {
                String googleHomeReport = String.format(GOOGLE_HOME_SERIES_ONE_EPISODE_REPORT,
                        seriesName,
                        schema.getEpisodes().get(0).getEpisodeNumber());
                googleHomeOutputService.broadcast(googleHomeReport);
            }
        }
    }

    void reportSummary() {
        log.info("UC303: report saved movies and series");
        StringBuilder sb = new StringBuilder();
        addSonarrToSummary(sb);
        addRadarrToSummary(sb);

        if (sb.length() > 0) {
            log.info("UC303: {} google home: broadcasting saved messages");
            googleHomeOutputService.broadcast(sb.toString());
        }

        sonarrService.resetNotYetReported();
        radarrService.resetNotYetReported();
    }

    private void addSonarrToSummary(StringBuilder sb) {
        Set<SonarrWebhookSchema> schemas = sonarrService.getNotYetReported();
        log.info("UC303: {} series saved for reporting", schemas.size());

        if (schemas.size() == 1) {
            sb.append(String.format(SUMMARY_MESSAGE_TEMPLATE, 1, "epsiode is"));
        } else
        if (schemas.size() > 1) {
            sb.append(String.format(SUMMARY_MESSAGE_TEMPLATE, schemas.size(), "episodes are"));
        }
    }

    private void addRadarrToSummary(StringBuilder sb) {
        Set<RadarrWebhookSchema> schemas = radarrService.getNotYetReported();
        log.info("UC303: {} movies saved for reporting", schemas.size());

        if (schemas.size() == 1) {
            sb.append(String.format(SUMMARY_MESSAGE_TEMPLATE, 1, "movie is"));
        }
        if (schemas.size() > 1) {
            sb.append(String.format(SUMMARY_MESSAGE_TEMPLATE, schemas.size(), "movies are"));
        }
    }
}
