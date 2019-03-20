package nl.dejagermc.homefeeder.input.groningen.garbishcollection;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.groningen.garbishcollection.enums.BinType;
import nl.dejagermc.homefeeder.input.groningen.garbishcollection.model.BinPickup;
import nl.dejagermc.homefeeder.input.groningen.garbishcollection.repository.BinPickupRepository;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static nl.dejagermc.homefeeder.input.groningen.garbishcollection.predicates.BinPickupPredicates.sortBinPickups;

@Service
@Slf4j
public class GarbageCollectionService {

    private BinPickupRepository binPickupRepository;
    private Set<BinPickup> notReported = new HashSet<>();

    @Autowired
    public GarbageCollectionService(BinPickupRepository binPickupRepository) {
        this.binPickupRepository = binPickupRepository;
    }

    public List<BinPickup> getAllBinPickups() {
        return binPickupRepository.getAllBinPickups();
    }

    public Optional<BinPickup> getNextBinPickup() {
        return getAllBinPickups().stream()
                .filter(bin -> bin.getPickupDay().isAfter(LocalDate.now()))
                .sorted(sortBinPickups())
                .findFirst();
    }

    public Optional<BinPickup> getNextBinPickupForType(BinType binType) {
        return getAllBinPickups().stream()
                .filter(bin -> bin.getPickupDay().isAfter(LocalDate.now()))
                .filter(bin -> bin.getBinType().equals(binType))
                .sorted(sortBinPickups())
                .findFirst();
    }

    public void addNotReported(BinPickup binPickup) {
        notReported.add(binPickup);
    }

    public List<BinPickup> getNotReported() {
        return new ArrayList<>(notReported);
    }

    public void resetNotReported() {
        notReported = new HashSet<>();
    }
}
