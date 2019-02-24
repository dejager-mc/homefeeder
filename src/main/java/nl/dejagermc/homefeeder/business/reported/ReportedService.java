package nl.dejagermc.homefeeder.business.reported;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reported.model.ReportedTo;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class ReportedService {
    private Set<Pair<Object, ReportedTo>> reportedObjects = new HashSet<>();

    @Scheduled(cron = "0 0 2 * * *")
    public void resetAll() {
        reportedObjects = new HashSet<>();
    }

    public boolean hasThisBeenReportedToThat(Object object, ReportedTo reportedTo) {
        return reportedObjects.stream().anyMatch(m -> m.getFirst().equals(object) && m.getSecond().equals(reportedTo));
    }

    public void markThisReportedToThat(Object object, ReportedTo reportedTo) {
        reportedObjects.add(Pair.of(object, reportedTo));
    }
}
