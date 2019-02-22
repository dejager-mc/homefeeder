package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.domain.generated.radarr.RemoteMovie;
import nl.dejagermc.homefeeder.domain.generated.sonarr.Episode;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.gathering.radarr.RadarrService;
import nl.dejagermc.homefeeder.gathering.sonarr.SonarrService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.business.reported.ReportedService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DownloadReportService extends AbstractReportService {

    private static final String TELEGRAM_MOVIE_REPORT = "<b>Movie available</b>%n%s (%s)%n<a href=https://www.imdb" +
            ".com/title/%s>%s</a>";
    private static final String GOOGLE_HOME_MOVIE_REPORT = "The movie %s is now available.";

    private static final String TELEGRAM_SERIES_REPORT = "<b>Episode available</b>%n%s - %sx%s - %s [%s]";
    private static final String GOOGLE_HOME_SERIES_ONE_EPISODE_REPORT = "Episode %s for series %s is now available.";
    private static final String GOOGLE_HOME_SERIES_MULTIPLE_EPISODES_REPORT = "%s episodes for series %s are now available.";

    private RadarrService radarrService;
    private SonarrService sonarrService;

    @Autowired
    public DownloadReportService(UserState userState, ReportedService reportedService, TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput, RadarrService radarrService, SonarrService sonarrService) {
        super(userState, reportedService, telegramOutput, googleHomeOutput);
        this.radarrService = radarrService;
        this.sonarrService = sonarrService;
    }

    public void reportRadarr(RadarrWebhookSchema schema) {
        RemoteMovie remoteMovie = schema.getRemoteMovie();

        String telegramReport = String.format(TELEGRAM_MOVIE_REPORT,
                remoteMovie.getTitle(),
                remoteMovie.getYear(),
                remoteMovie.getImdbId(),
                remoteMovie.getTitle());

        telegramOutput.sendMessage(telegramReport);

        if (!userState.reportNow()) {
            radarrService.addNotYetReported(schema);
        } else {
            String googleHomeReport = String.format(GOOGLE_HOME_MOVIE_REPORT,
                    remoteMovie.getTitle());

            googleHomeOutput.broadcast(googleHomeReport);
        }
    }

    public void reportSonarr(SonarrWebhookSchema schema) {
        String seriesName = schema.getSeries().getTitle();
        for (Episode episode : schema.getEpisodes()) {
            String telegramReport = String.format(TELEGRAM_SERIES_REPORT,
                    seriesName,
                    episode.getSeasonNumber(),
                    episode.getEpisodeNumber(),
                    episode.getTitle(),
                    episode.getQuality());
            telegramOutput.sendMessage(telegramReport);
        }

        if (!userState.reportNow()) {
            sonarrService.addNotYetReported(schema);
        } else {
            if (schema.getEpisodes().size() > 1) {
                String googleHomeReport = String.format(GOOGLE_HOME_SERIES_MULTIPLE_EPISODES_REPORT,
                        schema.getEpisodes().size(),
                        seriesName);
                googleHomeOutput.broadcast(googleHomeReport);
            } else if (schema.getEpisodes().size() == 1) {
                String googleHomeReport = String.format(GOOGLE_HOME_SERIES_ONE_EPISODE_REPORT,
                        schema.getEpisodes().get(0).getEpisodeNumber(),
                        seriesName);
                googleHomeOutput.broadcast(googleHomeReport);
            }
        }
    }
}
