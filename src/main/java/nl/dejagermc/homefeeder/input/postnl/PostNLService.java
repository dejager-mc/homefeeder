package nl.dejagermc.homefeeder.input.postnl;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.postnl.model.Delivery;
import nl.dejagermc.homefeeder.input.postnl.repository.DeliveryRepository;
import nl.dejagermc.homefeeder.util.selenium.HeadlessChrome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.postnl.predicates.DeliveryPredicates.isDeliveryForToday;

@Slf4j
@Service
public class PostNLService {
    private DeliveryRepository deliveryRepository;

    @Autowired
    private HeadlessChrome headlessChrome;

    @Autowired
    public PostNLService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public Set<Delivery> getAllDeliveries() {
        return deliveryRepository.getAllDeliveries();
    }

    public Set<Delivery> getTodaysDeliveries() {
        return deliveryRepository.getAllDeliveries().stream()
                .filter(isDeliveryForToday())
                .collect(Collectors.toSet());
    }

    public void test() {
        deliveryRepository.getAllDeliveries();
    }
}
