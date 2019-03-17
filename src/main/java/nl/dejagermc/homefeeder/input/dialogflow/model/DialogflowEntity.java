package nl.dejagermc.homefeeder.input.dialogflow.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors
@Getter
@Setter
@ToString
public class DialogflowEntity {
    private String session;
    private String action;
    private List<String> items;
    private String actionType;
}