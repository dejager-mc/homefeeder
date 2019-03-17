package nl.dejagermc.homefeeder.input.openhab.repository;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.openhab.model.OpenhabItem;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OpenhabItemRepository {

    @Value("${openhab.rest}")
    private String uriRestBase;
    private static final String ITEMS = "items";

    private HttpUtil httpUtil;

    @Autowired
    public OpenhabItemRepository(HttpUtil httpUtil) {
        this.httpUtil = httpUtil;
    }

    @Cacheable(cacheNames = "getAllOpenhabItems", cacheManager = "cacheManagerCaffeine")
    public Set<OpenhabItem> getAllOpenhabItems() {
        Optional<Document> document = httpUtil.getDocumentIgnoreContentType(uriRestBase + ITEMS);
        if (document.isPresent()) {
            String text = document.get().body().text();
            text = text.substring(2,text.length()-2);
            List<String> things = Arrays.asList(text.split("\\},\\{"));
            return things.stream().map(this::convertJsonTextToOpenhabThing).collect(Collectors.toSet());
        }
        return Set.of();
    }

    private OpenhabItem convertJsonTextToOpenhabThing(final String text) {
        JSONObject jsonObject = new JSONObject("{" + text + "}");

        return OpenhabItem.builder()
                .label(getJsonObjectValueOrEmptyString(jsonObject, "label"))
                .link(getJsonObjectValueOrEmptyString(jsonObject, "link"))
                .state(getJsonObjectValueOrEmptyString(jsonObject, "state"))
                .name(getJsonObjectValueOrEmptyString(jsonObject, "name"))
                .type(getJsonObjectValueOrEmptyString(jsonObject, "type"))
                .build();
    }

    private String getJsonObjectValueOrEmptyString(JSONObject jsonObject, String value) {
        return jsonObject.has(value) ? (String)jsonObject.get(value) : "";
    }
}
