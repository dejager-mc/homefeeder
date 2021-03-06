package nl.dejagermc.homefeeder.business.dialogflow;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.streaming.StreamOutputBusinessService;
import nl.dejagermc.homefeeder.input.dialogflow.DialogflowInputService;
import nl.dejagermc.homefeeder.input.dialogflow.model.DialogflowEntity;
import nl.dejagermc.homefeeder.input.openhab.OpenhabInputService;
import nl.dejagermc.homefeeder.input.openhab.model.OpenhabItem;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutputService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.api.client.util.Preconditions.checkState;

@Service
@Slf4j
public class DialogflowBusinessService {
    private static final String ACTION_ON = "on";
    private static final String ACTION_OFF = "off";
    private static final String ACTION_STREAM = "stream dota";
    private static final String ACTION_REBOOT = "reboot";

    @Value("${homefeeder.dialogflow.project.id}")
    private String projectId;

    private OpenhabInputService openhabInputService;
    private StreamOutputBusinessService streamOutputBusinessService;
    private DialogflowInputService dialogflowInputService;
    private OpenhabOutputService openhabOutputService;

    @Inject
    public DialogflowBusinessService(OpenhabInputService openhabInputService, StreamOutputBusinessService streamOutputBusinessService, DialogflowInputService dialogflowInputService, OpenhabOutputService openhabOutputService) {
        this.openhabInputService = openhabInputService;
        this.streamOutputBusinessService = streamOutputBusinessService;
        this.dialogflowInputService = dialogflowInputService;
        this.openhabOutputService = openhabOutputService;
    }

    public void handleRequest(GoogleCloudDialogflowV2WebhookRequest request) {
        DialogflowEntity entity = dialogflowInputService.convertRequestToEntity(request);
        checkState(!entity.getActionType().isBlank(), "No actiontype in request.");
        checkState(!entity.getAction().isBlank(), "No action in request.");
        checkState(!entity.getSession().isBlank(), "No session in request.");
        checkState(!entity.getItems().isEmpty(), "No items in request.");

        if (!entity.getSession().matches(".*" + projectId + ".*")) {
            log.info("UC400: request was not made by the correct google actions project.");
            return;
        }
        if (!entity.getAction().equals("perform.action.on.items")) {
            log.info("UC400: action is not: perform.action.on.items");
            return;
        }

        performAction(entity);
    }

    private void performAction(DialogflowEntity entity) {
        if (entity.getActionType().equalsIgnoreCase(ACTION_ON) || entity.getActionType().equalsIgnoreCase(ACTION_OFF)) {
            performActionSwitch(entity);
        }
        if (entity.getActionType().equalsIgnoreCase(ACTION_STREAM)) {
            performActionStream(entity);
        }
        if (entity.getActionType().equalsIgnoreCase(ACTION_REBOOT)) {
            performActionReboot(entity);
        }
    }

    private void performActionSwitch(DialogflowEntity entity) {
        List<OpenhabItem> items = getAllOpenhabItemsForRequest(entity);
        items.forEach(item -> log.info("UC402: switching {} {}", item.getLabel(), entity.getActionType()));
        items.forEach(item -> openhabOutputService.performActionOnSwitchItem(entity.getActionType(), item));
    }

    private void performActionStream(DialogflowEntity entity) {
        List<OpenhabItem> items = getAllOpenhabItemsForRequest(entity);
        items.forEach(item -> log.info("UC403: starting stream on {}", item));
        streamOutputBusinessService.streamLiveMatch(items);
    }

    private void performActionReboot(DialogflowEntity entity) {
        List<OpenhabItem> items = getAllOpenhabItemsForRequest(entity, " reboot");
        items.forEach(item -> log.info("UC402: rebooting {}", item.getLabel()));
        items.forEach(item -> openhabOutputService.performActionOnSwitchItem("ON", item));
    }

    private List<OpenhabItem> getAllOpenhabItemsForRequest(DialogflowEntity entity) {
        return getAllOpenhabItemsForRequest(entity, "");
    }

    private List<OpenhabItem> getAllOpenhabItemsForRequest(DialogflowEntity entity, String labelAddition) {
        List<OpenhabItem> items = entity.getItems().stream()
                .map(item -> openhabInputService.findOpenhabItemWithLabel(item + labelAddition))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if (items.size() != entity.getItems().size()) {
            log.error("UC400: Expected {} items but only found {}. Found items: [{}]. Requested items: [{}]",
                    items.size(),
                    entity.getItems().size(),
                    items,
                    entity.getItems().stream()
                            .map(item -> item + labelAddition)
                            .collect(Collectors.toList())
            );
            return Collections.emptyList();
        }
        return items;
    }
}