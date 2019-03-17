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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        return "What information do you collect?: I do not collect any information.</br>How do you use the information?: I do not collect any information.</br>What information do you share?: I do not collect any information.";
    }

    @PostMapping("webhook")
    public GoogleCloudDialogflowV2WebhookResponse webhook(@RequestBody String rawRequest) {
        log.info("UC400: dialogflow request.");
        handleRequest(rawRequest);
        return generateResponse();
    }

    private void handleRequest(final String rawRequest) {
        try (JsonParser request = jacksonFactory.createJsonParser(rawRequest) ) {
            GoogleCloudDialogflowV2WebhookRequest dialogRequest = request.parse(GoogleCloudDialogflowV2WebhookRequest.class);
            dialogflowBusinessService.handleRequest(dialogRequest);
        } catch (IOException e) {
            log.info("UC400: error handling request: {}", e);
        }
    }

    private GoogleCloudDialogflowV2WebhookResponse generateResponse() {
        log.info("UC400: sending response.");
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();
        response.setFulfillmentText(getRandomResponseText());
        return response;
    }

    private String getRandomResponseText() {
        List<String> responses = Arrays.asList("F.A.B.", "Affirmative.", "It is done.", "As you will.", "As you command.", "An excellent choice.");
        return responses.get(new Random().nextInt(responses.size()));
    }
}
