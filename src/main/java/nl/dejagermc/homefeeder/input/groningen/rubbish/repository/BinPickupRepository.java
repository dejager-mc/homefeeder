package nl.dejagermc.homefeeder.input.groningen.rubbish.repository;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.groningen.rubbish.enums.BinType;
import nl.dejagermc.homefeeder.input.groningen.rubbish.model.BinPickup;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.groningen.rubbish.enums.BinType.*;

@Component
@Slf4j
public class BinPickupRepository {

    private static final String URI = "https://gemeente.groningen.nl/afvalwijzer/groningen/9734BB/63/%s";
    private JsoupUtil jsoupUtil;

    @Autowired
    public BinPickupRepository(JsoupUtil jsoupUtil) {
        this.jsoupUtil = jsoupUtil;
    }

    @Cacheable(cacheNames = "getAllBinPickups", cacheManager = "cacheManagerCaffeine")
    public List<BinPickup> getAllBinPickups() {
        Elements elements = getAllElements();
        List<BinPickup> pickupList = new ArrayList<>();
        for (Element element : elements) {
            pickupList.addAll(mapElementToBinPickups(element));
        }
        return pickupList;
    }

    private Elements getAllElements() {
        Optional<Document> optionalDoc = jsoupUtil.getDocument(getUri());
        if (optionalDoc.isPresent()) {
            return optionalDoc.get().select("tbody > tr");
        }

        return new Elements();
    }

    private List<BinPickup> mapElementToBinPickups(Element element) {
        List<BinPickup> binPickupList = new ArrayList<>();
        BinType binType = getBinType(element);
        if (null == binType) {
            return Collections.emptyList();
        }
        Elements monthElements = element.select("td");
        for (int i = 0; i < monthElements.size(); i++) {
            int month = i + 1;
            List<Integer> days = monthElements.get(i).select("li").stream()
                    .filter(e -> !e.text().equals("(afwijkende ophaaldag)"))
                    .map(e -> Integer.parseInt(e.text()))
                    .collect(Collectors.toList());
            for (int day : days) {
                binPickupList.add(createBinPickup(binType, month, day));
            }
        }
        return binPickupList;
    }

    private BinPickup createBinPickup(BinType binType, int month, int day) {
        BinPickup binPickup = new BinPickup();
        binPickup.setBinType(binType);
        binPickup.setPickupDay(LocalDate.of(LocalDate.now().getYear(), month, day));
        return binPickup;
    }

    private BinType getBinType(Element element) {
        String type = element.select("h2").text();
        if (BLUE.getName().equals(type)) {
            return BLUE;
        }
        if (GRAY.getName().equals(type)) {
            return GRAY;
        }
        if (GREEN.getName().equals(type)) {
            return GREEN;
        }
        return null;
    }

    private String getUri() {
        return String.format(URI, LocalDate.now().getYear());
    }
}
