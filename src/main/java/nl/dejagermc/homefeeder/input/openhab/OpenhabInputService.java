package nl.dejagermc.homefeeder.input.openhab;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.openhab.model.OpenhabThing;
import nl.dejagermc.homefeeder.input.openhab.repository.OpenhabThingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenhabInputService {

    private OpenhabThingRepository openhabThingRepository;

    @Autowired
    public OpenhabInputService(OpenhabThingRepository openhabThingRepository) {
        this.openhabThingRepository = openhabThingRepository;
    }

    public Set<OpenhabThing> getAllOpenhabThings() {
        return openhabThingRepository.getAllOpenhabThings();
    }

    public Optional<OpenhabThing> findOpenhabThing(String value) {
        List<OpenhabThing> results = getAllOpenhabThings().stream().filter(thing -> thing.getLabel().equalsIgnoreCase(value)).collect(Collectors.toList());
        if (results.size() > 1) {
            log.error("More than 1 thing found for value: {}. Found is: {}", value, results);
            return Optional.empty();
        }
        if (results.size() == 1) {
            return Optional.of(results.get(0));
        }

        return Optional.empty();
    }
}
