package nl.dejagermc.homefeeder.business.dialogflow.builder;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2QueryResult;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogflowRequestBuilder {

    public static GoogleCloudDialogflowV2WebhookRequest dialogflowRequest(String actionType, List<String> items) {
        GoogleCloudDialogflowV2WebhookRequest request = defaultDialogflowRequest();
        request.setQueryResult(getQueryResult(actionType, items));
        return request;
    }

    public static GoogleCloudDialogflowV2WebhookRequest dialogflowRequestKitchenLightsOff() {
        GoogleCloudDialogflowV2WebhookRequest request = defaultDialogflowRequest();
        request.setQueryResult(getQueryResultSwitchOFF());
        return request;
    }
    public static GoogleCloudDialogflowV2WebhookRequest dialogflowRequestKitchenLightsOn() {
        GoogleCloudDialogflowV2WebhookRequest request = defaultDialogflowRequest();
        request.setQueryResult(getQueryResultSwitchON());
        return request;
    }

    private static GoogleCloudDialogflowV2QueryResult getQueryResult(String actionType, List<String> items) {
        GoogleCloudDialogflowV2QueryResult result = new GoogleCloudDialogflowV2QueryResult();

        result.setAction("perform.action.on.items");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("action-type", actionType);
        parameters.put("item", items);

        result.setParameters(parameters);

        return result;
    }
    private static GoogleCloudDialogflowV2QueryResult getQueryResultSwitchON() {
        GoogleCloudDialogflowV2QueryResult result = new GoogleCloudDialogflowV2QueryResult();

        result.setAction("perform.action.on.items");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("action-type", "ON");
        List<String> items = List.of("kitchen lights");
        parameters.put("item", items);

        result.setParameters(parameters);

        return result;
    }

    private static GoogleCloudDialogflowV2QueryResult getQueryResultSwitchOFF() {
        GoogleCloudDialogflowV2QueryResult result = new GoogleCloudDialogflowV2QueryResult();

        result.setAction("perform.action.on.items");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("action-type", "OFF");
        List<String> items = List.of("kitchen lights");
        parameters.put("item", items);

        result.setParameters(parameters);

        return result;
    }

    private static GoogleCloudDialogflowV2WebhookRequest defaultDialogflowRequest() {
        GoogleCloudDialogflowV2WebhookRequest request = new GoogleCloudDialogflowV2WebhookRequest();
        request.setSession("homecontrol-eb9a5");
        return request;
    }
}
