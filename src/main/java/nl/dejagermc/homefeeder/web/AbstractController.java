package nl.dejagermc.homefeeder.web;

import nl.dejagermc.homefeeder.user.UserState;

public class AbstractController {

    UserState userState;

    public AbstractController(UserState userState) {
        this.userState = userState;
    }
}
