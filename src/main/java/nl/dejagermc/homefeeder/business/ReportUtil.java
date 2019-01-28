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

    public StringBuilder addDownloadedMoviesToReport(StringBuilder sb) {
        if (userState.radarrDownloads().isEmpty()) {
            return sb;
        }
        if (userState.radarrDownloads().size()==1) {
            sb.append("1 movie has been downloaded: ");
        } else {
            sb.append(userState.radarrDownloads().size() + " movies have been downloaded: ");
        }
        userState.radarrDownloads().stream()
                .forEach(movie -> sb.append(movie.getTitle()).append(", "));
        sb.append(System.lineSeparator());
        return sb;
    }
}
