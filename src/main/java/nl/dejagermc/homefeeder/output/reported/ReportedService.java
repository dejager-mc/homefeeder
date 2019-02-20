package nl.dejagermc.homefeeder.output.reported;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.output.reported.model.ReportedTo;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class ReportedService {
    private Set<Pair<Object, ReportedTo>> reportedObjects = new HashSet<>();

    public void resetAll() {
        reportedObjects = new HashSet<>();
    }

    public boolean hasThisBeenReported(Object object, ReportedTo reportedTo) {
        log.info("hasThisBeenReported: {}, {}", object, reportedTo);
        return reportedObjects.stream().anyMatch(m -> m.getFirst().equals(object) && m.getSecond().equals(reportedTo));
    }

    public void reportThisToThat(Object object, ReportedTo reportedTo) {
        reportedObjects.add(Pair.of(object, reportedTo));
    }
}
