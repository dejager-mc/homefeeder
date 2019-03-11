package nl.dejagermc.homefeeder.input.openhab.repository;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.openhab.model.OpenhabThing;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
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
public class OpenhabThingRepository {

    @Value("${openhab.rest}")
    private String uri;

    private JsoupUtil jsoupUtil;

    @Autowired
    public OpenhabThingRepository(JsoupUtil jsoupUtil) {
        this.jsoupUtil = jsoupUtil;
    }

    @Cacheable(cacheNames = "getAllOpenhabThings", cacheManager = "cacheManagerCaffeine")
    public Set<OpenhabThing> getAllOpenhabThings() {
        Optional<Document> document = jsoupUtil.getDocumentIgnoreContentType(uri);
        if (document.isPresent()) {
            String text = document.get().body().text();
            text = text.substring(2,text.length()-2);
            List<String> things = Arrays.asList(text.split("\\},\\{"));
            return things.stream().map(this::convertJsonTextToOpenhabThing).collect(Collectors.toSet());
        }
        return Set.of();
    }

    private OpenhabThing convertJsonTextToOpenhabThing(final String text) {
        JSONObject jsonObject = new JSONObject("{" + text + "}");

        OpenhabThing thing = new OpenhabThing();
        thing.setLink(getJsonObjectValueOrEmptyString(jsonObject, "link"));
        thing.setLabel(getJsonObjectValueOrEmptyString(jsonObject, "label"));
        thing.setState(getJsonObjectValueOrEmptyString(jsonObject, "state"));
        thing.setName(getJsonObjectValueOrEmptyString(jsonObject, "name"));
        thing.setType(getJsonObjectValueOrEmptyString(jsonObject, "type"));

        return thing;
    }

    private String getJsonObjectValueOrEmptyString(JSONObject jsonObject, String value) {
        return jsonObject.has(value) ? (String)jsonObject.get(value) : "";
    }
}
