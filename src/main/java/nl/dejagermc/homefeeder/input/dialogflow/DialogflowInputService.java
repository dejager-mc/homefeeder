package nl.dejagermc.homefeeder.input.dialogflow;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.dialogflow.model.DialogflowEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DialogflowInputService {

    public DialogflowInputService() {
        // empty
    }

    public DialogflowEntity convertRequestToEntity(GoogleCloudDialogflowV2WebhookRequest request) {
        DialogflowEntity entity = new DialogflowEntity();
        Map<String, Object> parameters = getParameters(request);

        entity.setAction(getAction(request));
        entity.setSession(getSession(request));
        entity.setActionType(getActionType(parameters).toUpperCase());
        entity.setItems(getItems(parameters));

        return entity;
    }

    private String getSession(GoogleCloudDialogflowV2WebhookRequest request) {
        return request.getSession();
    }

    private String getAction(GoogleCloudDialogflowV2WebhookRequest request) {
        return request.getQueryResult().getAction();
    }

    private Map<String, Object> getParameters(GoogleCloudDialogflowV2WebhookRequest request) {
        return request.getQueryResult().getParameters();
    }

    @SuppressWarnings("unchecked")
    private List<String> getItems(Map<String, Object> parameters) {
        return (List<String>) parameters.get("item");
    }

    private String getActionType(Map<String, Object> parameters) {
        return (String) parameters.get("action-type");
    }
}
