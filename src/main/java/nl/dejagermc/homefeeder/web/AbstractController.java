package nl.dejagermc.homefeeder.web;

import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;

public class AbstractController {

    HomeFeederState homeFeederState;

    public AbstractController(HomeFeederState homeFeederState) {
        this.homeFeederState = homeFeederState;
    }
}
