package nl.dejagermc.homefeeder.gathering.postnl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class PostNLUtil {
    private static final Logger LOG = LoggerFactory.getLogger(PostNLUtil.class);

    @Value("${postnl.login.email}")
    private String email;
    @Value("${postnl.login.password}")
    private String password;

    public void test() {
        try {
            //With this you login and a session is created
            Connection.Response res = Jsoup.connect("https://jouw.postnl.nl/?pst=k-pnl_f-f_p-pnl_u-txt_s-pwb_r-pnlinlogopties_v-jouwpost#!/inloggen")
                    .data("email", email, "password", password)
                    .method(Connection.Method.POST)
                    .execute();

            //This will get you cookies
            Map<String, String> loginCookies = res.cookies();

            //Here you parse the page that you want. Put the url that you see when you have logged in
            Document doc = Jsoup.connect("https://jouw.postnl.nl/?pst=k-pnl_f-f_p-pnl_u-txt_s-pwb_r-pnlinlogopties_v-jouwpost#!/overzicht")
                    .cookies(loginCookies)
                    .get();
            LOG.info(doc.toString());
        } catch (Exception e) {
            LOG.error("Exception postnl: ", e);
        }
    }
}
