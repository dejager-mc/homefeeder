package nl.dejagermc.homefeeder.gathering.postnl.repository;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.postnl.model.Delivery;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DeliveryRepository {
    private static final String POSTNL_LOGIN_URI = "";

    @Value("${postnl.login.email}")
    private String email;
    @Value("${postnl.login.password}")
    private String password;

    private JsoupUtil jsoupUtil;

    @Autowired
    public DeliveryRepository(JsoupUtil jsoupUtil) {
        this.jsoupUtil = jsoupUtil;
    }

    @Cacheable(cacheNames = "getAllDeliveries", cacheManager = "cacheManagerCaffeine")
    public Set<Delivery> getAllDeliveries() {
        Elements elements = getAllDeliveryElements();
        return convertElementsToDeliveries(elements);
    }

    private Elements getAllDeliveryElements() {
        Optional<Document> optionalDoc = jsoupUtil.getPostNlDeliveriesDocument(email, password);
        if (optionalDoc.isPresent()) {
            return optionalDoc.get().select("div");
        }

        return new Elements();
    }

    private Set<Delivery> convertElementsToDeliveries(Elements elements) {
        return elements.stream()
                .filter(Objects::nonNull)
                .map(this::buildDelivery)
                .collect(Collectors.toSet());
    }

    private Delivery buildDelivery(Element element) {
        return Delivery.builder()
                .sender(getSender(element))
                .weightInGrams(getWeightInGrams(element))
                .startTime(getStartTime(element))
                .endTime(getEndTime(element))
                .build();
    }

    private String getSender(Element element) {
        return "";
    }

    private int getWeightInGrams(Element element) {
        return 0;
    }

    private LocalDateTime getStartTime (Element element) {
        return LocalDateTime.now();
    }


    private LocalDateTime getEndTime (Element element) {
        return LocalDateTime.now();
    }
}
