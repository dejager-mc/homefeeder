package nl.dejagermc.homefeeder.business.dialogflow;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.streaming.StreamOutputBusinessService;
import nl.dejagermc.homefeeder.input.openhab.OpenhabInputService;
import nl.dejagermc.homefeeder.input.openhab.model.OpenhabThing;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.openhab.predicates.OpenhabThingPredicates.isThingAString;
import static nl.dejagermc.homefeeder.input.openhab.predicates.OpenhabThingPredicates.isThingASwitch;

@Service
@Slf4j
public class DialogflowBusinessService {

    @Value("${openhab.rest}")
    private String openhabApiUri;
    @Value("${homefeeder.dialogflow.project.id}")
    private String projectId;

    private OpenhabInputService openhabInputService;
    private JsoupUtil jsoupUtil;
    private StreamOutputBusinessService streamOutputBusinessService;

    @Autowired
    public DialogflowBusinessService(OpenhabInputService openhabInputService, JsoupUtil jsoupUtil, StreamOutputBusinessService streamOutputBusinessService) {
        this.openhabInputService = openhabInputService;
        this.jsoupUtil = jsoupUtil;
        this.streamOutputBusinessService = streamOutputBusinessService;
    }

    public void handleRequest(GoogleCloudDialogflowV2WebhookRequest request) {
        boolean isAuthenticated = isRequestMadeByHomeFeederAction(request);
        log.info("Request made by home feeder action: {}", isAuthenticated);

        Map<String, Object> parameters = getParameters(request);
        log.info("Parameters: {}", parameters.toString());
        log.info("Action: {}", getAction(request));
        log.info("Things: {}", getThings(parameters));
        log.info("Action type: {}", getActionType(parameters));

        DialogflowEntity dialogflowEntity = new DialogflowEntity();
        dialogflowEntity.setAction(getAction(request));
        dialogflowEntity.setActionType(getActionType(parameters));
        dialogflowEntity.setThings(getThings(parameters));

        performActionToSwitchThing(dialogflowEntity);
    }

    private void performActionToSwitchThing(DialogflowEntity dialogflowEntity) {
        if (!dialogflowEntity.getAction().equals("action.on.thing")) {
            log.info("Not an action.on.thing");
            return;
        }

        List<OpenhabThing> things = dialogflowEntity.getThings().stream()
                .map(openhabInputService::findOpenhabThing)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        things.stream()
                .filter(isThingASwitch())
                .forEach(thing -> controlSwitchThing(thing, dialogflowEntity));
        things.stream()
                .filter(isThingAString())
                .forEach(thing -> streaming(thing, dialogflowEntity));
    }

    private void controlSwitchThing(OpenhabThing thing, DialogflowEntity dialogflowEntity) {
        log.info("Thing found to control: {}", thing);
        if (dialogflowEntity.getActionType().equals("on")) {
            jsoupUtil.postJsonToOpenhab(thing.getLink(), "ON");
            return;
        }
        if (dialogflowEntity.getActionType().equals("off")) {
            jsoupUtil.postJsonToOpenhab(thing.getLink(), "OFF");
            return;
        }
        log.error("Control Switch Thing called without on or off action. Thing: {}. Dialogflow: {}", thing, dialogflowEntity);
    }

    private void streaming(OpenhabThing thing, DialogflowEntity dialogflowEntity) {
        log.info("Streaming dota");
        if (dialogflowEntity.getActionType().equals("stream")) {
            streamOutputBusinessService.streamLiveMatch();
            return;
        }
        log.error("String Thing called without stream action. Thing: {}. Dialogflow: {}", thing, dialogflowEntity);
    }

    private boolean isRequestMadeByHomeFeederAction(GoogleCloudDialogflowV2WebhookRequest request) {
        return request.getSession().matches(".*" + projectId + ".*");
    }

    private String getAction(GoogleCloudDialogflowV2WebhookRequest request) {
        return request.getQueryResult().getAction();
    }

    private Map<String, Object> getParameters(GoogleCloudDialogflowV2WebhookRequest request) {
        return request.getQueryResult().getParameters();
    }

    @SuppressWarnings("unchecked")
    private List<String> getThings(Map<String, Object> parameters) {
        return (List<String>) parameters.get("thing");
    }

    private String getActionType(Map<String, Object> parameters) {
        return (String) parameters.get("action-type");
    }
}

@Component
@Accessors
@Getter
@Setter
@ToString
class DialogflowEntity {
    private String action;
    private List<String> things;
    private String actionType;
}