package nl.dejagermc.homefeeder.business;

import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportUtil {

    private UserState userState;

    @Autowired
    public ReportUtil(UserState userState) {
        this.userState = userState;
    }

//    public StringBuilder addDownloadedMoviesToReport(StringBuilder sb) {
//        if (userState.radarrDownloads().isEmpty()) {
//            sb.append("No new movies downloaded.");
//            return sb;
//        }
//        if (userState.radarrDownloads().size()==1) {
//            sb.append("1 movie has been downloaded: ");
//        } else {
//            sb.append(userState.radarrDownloads().size() + " movies have been downloaded: ");
//        }
//        userState.radarrDownloads().stream()
//                .forEach(movie -> sb.append(movie.getTitle()).append(", "));
//        return sb;
//    }
//
//    public StringBuilder addDownloadedSeriesToReport(StringBuilder sb) {
//        if (userState.sonarrDownloads().isEmpty()) {
//            sb.append("No new series downloaded.");
//            return sb;
//        }
//        if (userState.sonarrDownloads().size()==1) {
//            sb.append("1 series has been downloaded: ");
//        } else {
//            sb.append(userState.radarrDownloads().size() + " series have been downloaded: ");
//        }
//        userState.sonarrDownloads().stream()
//                .forEach(serie -> sb.append(serie.getTitle()).append(", "));
//        return sb;
//    }
//
//    public StringBuilder addActiveTournamentToReport(StringBuilder sb) {
//        Optional<Tournament> tournament = TournamentUtil.getMostImportantActiveTournament(userState.tournaments());
//        if (tournament.isPresent()) {
//            sb.append("The most important active dota tournament is " + tournament.get().name());
//        } else {
//            sb.append("There is no active dota tournament.");
//        }
//        return sb;
//    }
//
//    public StringBuilder addActiveMatchToReport(StringBuilder sb) {
//        List<Match> matches = userState.matches();
//        Optional<Tournament> tournament = TournamentUtil.getMostImportantActiveTournament(userState.tournaments());
//        if (tournament.isPresent()) {
//            sb.append("The most important active dota tournament is " + tournament.get().name());
//        } else {
//            sb.append("There is no active dota tournament.");
//        }
//        return sb;
//    }
}
