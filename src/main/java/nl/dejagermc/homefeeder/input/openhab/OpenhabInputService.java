package nl.dejagermc.homefeeder.input.openhab;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.openhab.model.OpenhabItem;
import nl.dejagermc.homefeeder.input.openhab.repository.OpenhabItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenhabInputService {

    private OpenhabItemRepository openhabItemRepository;

    @Autowired
    public OpenhabInputService(OpenhabItemRepository openhabItemRepository) {
        this.openhabItemRepository = openhabItemRepository;
    }

    public Set<OpenhabItem> getAllOpenhabItems() {
        return openhabItemRepository.getAllOpenhabItems();
    }

    public Optional<OpenhabItem> findOpenhabItemWithName(String name) {
        List<OpenhabItem> results = getAllOpenhabItems().stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
        if (results.size() > 1) {
            log.error("More than 1 item found for name: {}. Found is: {}", name, results);
            return Optional.empty();
        }
        if (results.size() == 1) {
            return Optional.of(results.get(0));
        }

        return Optional.empty();
    }

    public Optional<OpenhabItem> findOpenhabItemWithLabel(String label) {
        List<OpenhabItem> results = getAllOpenhabItems().stream()
                .filter(item -> item.getLabel().equalsIgnoreCase(label))
                .collect(Collectors.toList());
        if (results.size() > 1) {
            log.error("More than 1 item found for label: {}. Found is: {}", label, results);
            return Optional.empty();
        }
        if (results.size() == 1) {
            return Optional.of(results.get(0));
        }

        return Optional.empty();
    }
}