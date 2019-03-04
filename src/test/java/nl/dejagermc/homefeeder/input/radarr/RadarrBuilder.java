package nl.dejagermc.homefeeder.input.radarr;

import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.domain.generated.radarr.RemoteMovie;

public class RadarrBuilder {

    public static RadarrWebhookSchema getDefaultRadarrSchema() {
        RadarrWebhookSchema schema = new RadarrWebhookSchema();
        RemoteMovie remoteMovie = new RemoteMovie();
        remoteMovie.setTitle("Test Movie");
        schema.setRemoteMovie(remoteMovie);
        return schema;
    }
}
