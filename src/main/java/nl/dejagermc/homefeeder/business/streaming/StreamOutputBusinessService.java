package nl.dejagermc.homefeeder.business.streaming;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.util.MatchUtil;
import nl.dejagermc.homefeeder.input.openhab.OpenhabInputService;
import nl.dejagermc.homefeeder.input.openhab.model.OpenhabItem;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.MatchPredicates.isMatchWithStream;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.TournamentPredicates.sortTournamentsByImportanceMostToLeast;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.util.MatchUtil.getStreamUri;

@Service
@Slf4j
public class StreamOutputBusinessService {

    private static final String NO_MATCH_FOUND_MESSAGE = "There is no match that can be streamed.";
    private static final String MATCH_FOUND_MESSAGE = "Streaming %s versus %s.";

    private OpenhabOutputService openhabOutputService;
    private OpenhabInputService openhabInputService;
    private MatchService matchService;
    private TournamentService tournamentService;
    private GoogleHomeOutputService googleHomeOutputService;
    private SettingsService settingsService;

    @Inject
    public StreamOutputBusinessService(OpenhabOutputService openhabOutputService, SettingsService settingsService, MatchService matchService, TournamentService tournamentService, GoogleHomeOutputService googleHomeOutputService, OpenhabInputService openhabInputService) {
        this.openhabOutputService = openhabOutputService;
        this.settingsService = settingsService;
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.googleHomeOutputService = googleHomeOutputService;
        this.openhabInputService = openhabInputService;
    }

    public void streamLiveMatch(List<OpenhabItem> devices) {
        Optional<Match> match = getMostImportantLiveMatch();

        if (match.isPresent()) {
            Match liveMatch = match.get();
            googleHomeOutputService.broadcast(String.format(MATCH_FOUND_MESSAGE, liveMatch.leftTeam(), liveMatch.rightTeam()));
            devices.forEach(device -> turnOnDeviceAndStartStream(device, getStreamUri(liveMatch)));
        } else {
            googleHomeOutputService.broadcast(NO_MATCH_FOUND_MESSAGE);
        }
    }

    private void turnOnDeviceAndStartStream(OpenhabItem device, String streamUri) {
        Optional<OpenhabItem> steamItem = openhabInputService.findOpenhabItemWithLabel(device.getLabel() + " stream");
        if (steamItem.isPresent()) {
            log.info("UC403: starting stream on {}", device.getLabel());
            // turn on device
            openhabOutputService.performActionOnSwitchItem("ON", device);
            // start stream
            openhabOutputService.performActionOnStringItem(streamUri, steamItem.get());
        } else {
            log.error("UC403: could not find openhab stream item for {}", device);
        }
    }

    private Optional<Match> getMostImportantLiveMatch() {
        Set<Match> streamableLiveMatches = matchService.getLiveMatches().stream()
                .filter(isMatchWithStream())
                .collect(Collectors.toSet());

        if (streamableLiveMatches.isEmpty()) {
            return Optional.empty();
        }

        List<Tournament> sortedTournamentsWithLiveMatches = streamableLiveMatches.stream()
                .map(match -> tournamentService.getTournamentByName(match.tournamentName()))
                .distinct()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(sortTournamentsByImportanceMostToLeast())
                .collect(Collectors.toList());

        List<String> teams = settingsService.getFavoriteDotaTeams();
        Optional<Match> possibleMatch = getFirstMatchForTournamentAndFavTeam(sortedTournamentsWithLiveMatches, streamableLiveMatches, teams);

        if (possibleMatch.isPresent()) {
            return possibleMatch;
        }

        return getFirstMatchForTournament(sortedTournamentsWithLiveMatches, streamableLiveMatches);
    }

    private Optional<Match> getFirstMatchForTournamentAndFavTeam(final List<Tournament> sortedTournaments,
                                                                 final Set<Match> matches, final List<String> favTeams) {
        for (Tournament tournament : sortedTournaments) {
            Optional<Match> optionalMatch = matches.stream()
                    .filter(m -> m.isInTournament(tournament.name()))
                    .filter(m -> m.matchEitherTeam(favTeams))
                    .findFirst();
            if (optionalMatch.isPresent()) {
                return optionalMatch;
            }
        }
        return Optional.empty();
    }

    private Optional<Match> getFirstMatchForTournament(final List<Tournament> sortedTournaments,
                                                       final Set<Match> matches) {
        for (Tournament tournament : sortedTournaments) {
            Optional<Match> optionalMatch = matches.stream()
                    .filter(m -> m.isInTournament(tournament.name()))
                    .findFirst();
            if (optionalMatch.isPresent()) {
                return optionalMatch;
            }
        }
        return Optional.empty();
    }
}
