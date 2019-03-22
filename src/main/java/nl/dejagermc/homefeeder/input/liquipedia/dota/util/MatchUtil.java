package nl.dejagermc.homefeeder.input.liquipedia.dota.util;

import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;

public abstract class MatchUtil {
    private static final String TWITCH_BASE_URI = "https://www.twitch.tv/";
    private static final String YOUTUBE_BASE_URI = "https://www.youtube.com/channel/";

    private MatchUtil() {
        // util class
    }

    public static String getStreamUri(Match match) {
        if (!match.twitchChannel().isBlank()) {
            return TWITCH_BASE_URI + match.twitchChannel();
        }
        if (!match.youtubeChannel().isBlank()) {
            return YOUTUBE_BASE_URI + match.youtubeChannel();
        }
        return "";
    }
}
