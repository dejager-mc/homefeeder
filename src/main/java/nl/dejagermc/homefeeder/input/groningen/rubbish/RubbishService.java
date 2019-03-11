package nl.dejagermc.homefeeder.input.groningen.rubbish;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.groningen.rubbish.enums.BinType;
import nl.dejagermc.homefeeder.input.groningen.rubbish.model.BinPickup;
import nl.dejagermc.homefeeder.input.groningen.rubbish.repository.BinPickupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static nl.dejagermc.homefeeder.input.groningen.rubbish.predicates.BinPickupPredicates.sortBinPickups;

@Service
@Slf4j
public class RubbishService {

    private BinPickupRepository binPickupRepository;

    @Autowired
    public RubbishService(BinPickupRepository binPickupRepository) {
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
}
