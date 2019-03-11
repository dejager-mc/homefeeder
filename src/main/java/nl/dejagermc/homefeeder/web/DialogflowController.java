package nl.dejagermc.homefeeder.web;

import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.dialogflow.DialogflowBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("dialogflow")
@Slf4j
public class DialogflowController {

    private static JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
    private DialogflowBusinessService dialogflowBusinessService;

    @Autowired
    public DialogflowController(DialogflowBusinessService dialogflowBusinessService) {
        this.dialogflowBusinessService = dialogflowBusinessService;
    }

    @GetMapping("privacy")
    public String privacy() {
        return "What information do you collect?: none</br>How do you use the information?: I do not</br>What information do you share?: none.";
    }

    @PostMapping("webhook")
    public GoogleCloudDialogflowV2WebhookResponse webhook(@RequestBody String rawRequest) {
        handleRequest(rawRequest);
        return generateResponse();
    }

    private void handleRequest(final String rawRequest) {
        try (JsonParser request = jacksonFactory.createJsonParser(rawRequest) ) {
            GoogleCloudDialogflowV2WebhookRequest dialogRequest = request.parse(GoogleCloudDialogflowV2WebhookRequest.class);
            dialogflowBusinessService.handleRequest(dialogRequest);
        } catch (IOException e) {
            log.info("dialogflow request error: {}", e);
        }
    }

    private GoogleCloudDialogflowV2WebhookResponse generateResponse() {
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();
        response.setFulfillmentText("Affirmative Max");
        return response;
    }
}
