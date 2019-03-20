package nl.dejagermc.homefeeder.business.reported;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@Singleton
public class ReportedBusinessService {
    private Set<Pair<Object, ReportMethod>> reportedObjects = new HashSet<>();

    @Scheduled(cron = "0 0 2 * * *")
    public void resetAll() {
        log.info("UC020: Resetting reported cache.");
        reportedObjects = new HashSet<>();
    }

    public void reportOrSave(String message, Object toSave, ReportMethod reportMethod, Set<Object> savedObjects) {
        /*
        if user is listening
        report
        else
        save object to list
         */
    }

    public boolean hasThisBeenReportedToThat(Object object, ReportMethod reportMethod) {
        return reportedObjects.stream().anyMatch(m -> m.getFirst().equals(object) && m.getSecond().equals(reportMethod));
    }

    public void markThisReportedToThat(Object object, ReportMethod reportMethod) {
        reportedObjects.add(Pair.of(object, reportMethod));
    }
}
