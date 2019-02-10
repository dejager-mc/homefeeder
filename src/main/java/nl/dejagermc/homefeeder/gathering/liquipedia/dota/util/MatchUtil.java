package nl.dejagermc.homefeeder.gathering.liquipedia.dota.util;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class MatchUtil {
    private static final String TWITCH_BASE_URI = "https://www.twitch.tv/";
    private static final String YOUTUBE_BASE_URI = "https://www.youtube.com/channel/";

    public static String getStreamUri(Match match) {
        if (!match.twitchChannel().isBlank()) {
            return TWITCH_BASE_URI + match.twitchChannel();
        } else {
            return YOUTUBE_BASE_URI + match.youtubeChannel();
        }
    }
}
