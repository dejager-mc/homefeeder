package nl.dejagermc.homefeeder.input.postnl.repository;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.postnl.model.Delivery;
import nl.dejagermc.homefeeder.util.jsoup.JsoupUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DeliveryRepository {
    private static final String POSTNL_LOGIN_URI = "https://jouw.postnl.nl/?pst=k-pnl_f-f_p-pnl_u-txt_s-pwb_r-pnlinlogopties_v-jouwpost#!/inloggen?returnUrl=https:%2F%2Fwww.postnl.nl%2F";

    @Value("${postnl.login.email}")
    private String email;
    @Value("${postnl.login.password}")
    private String password;

    private JsoupUtil jsoupUtil;

    @Autowired
    public DeliveryRepository(JsoupUtil jsoupUtil) {
        this.jsoupUtil = jsoupUtil;
    }

    @Cacheable(cacheNames = "getAllDeliveries", cacheManager = "cacheManagerCaffeine")
    public Set<Delivery> getAllDeliveries() {
        Elements elements = getAllDeliveryElements();
        return convertElementsToDeliveries(elements);
    }

    // login to postnl
    public void test() {
        log.info("Attempting to log in to postnl website");
        getPageWithJBrowser();
    }

    private void getPageWithJBrowser() {
        JBrowserDriver driver = new JBrowserDriver(Settings.builder().
                timezone(Timezone.AMERICA_NEWYORK).build());

        // This will block for the page load and any
        // associated AJAX requests
        driver.get("https://jouw.postnl.nl/#!/overzicht");

        // You can getAllOpenhabThings status code unlike other Selenium drivers.
        // It blocks for AJAX requests and page loads after clicks
        // and keyboard events.
        System.out.println(driver.getStatusCode());

        // Returns the page source in its current state, including
        // any DOM updates that occurred after page load
        System.out.println(driver.getPageSource());

        // Close the browser. Allows this thread to terminate.
        driver.quit();
    }

    private String getLoginUri() {
        Optional<Document> doc = jsoupUtil.getDocument(POSTNL_LOGIN_URI);
        if (doc.isPresent()) {
            return doc.get().select("a#consumer-login-link").attr("href");
        }
        log.info("No uri found");
        return "";
    }

    private void login() {
        try {
//            grant_type	password
//            client_id	pwWebApp
//            username	onlineshopping.maxhunt@gmail.com
//            password	154411mc
            Connection.Response res = Jsoup.connect("https://jouw.postnl.nl/web/token")
                    .data("grant_type", "password")
                    .data("client_id", "pwWebApp")
                    .data("username", email)
                    .data("password", password)
                    .method(Connection.Method.POST)
                    .header("Accept","application/json, text/plain, */*")
                    .header("Accept-Encoding","gzip, deflate, br")
                    .header("Connection","keepl-alive")
                    .header("Content-Type","application/x-www-form-urlencoded")
                    .header("Cookie","Language=nl; ely_cc_answ={\"pri…2ZXJ6aWNodDojIS9vdmVyemljaHQ=")
                    .header("Host","jouw.postnl.nl")
                    .header("Referer","https://jouw.postnl.nl/?pst=k-…wb_r-pnlinlogopties_v-jouwpost")
                    .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; …) Gecko/20100101 Firefox/65.0")
                    .execute();
            // https://stackoverflow.com/questions/30406264/cannot-login-to-website-by-using-jsoup-with-x-www-form-urlencoded-parameters

//            Accept
//            application/json, text/plain, */*
//Accept-Encoding
//gzip, deflate, br
//Accept-Language
//nl,en-US;q=0.7,en;q=0.3
//Connection
//keep-alive
//Content-Length
//100
//Content-Type
//application/x-www-form-urlencoded
//Cookie
//Language=nl; ely_cc_answ={"pri…2ZXJ6aWNodDojIS9vdmVyemljaHQ=
//DNT
//1
//Host
//jouw.postnl.nl
//Referer
//https://jouw.postnl.nl/?pst=k-…wb_r-pnlinlogopties_v-jouwpost
//TE
//Trailers
//User-Agent
//Mozilla/5.0 (Windows NT 10.0; …) Gecko/20100101 Firefox/65.0

            Map<String, String> loginCookies = res.cookies();

            //Here you parse the page that you want. Put the url that you see when you have logged in
            Document doc = Jsoup.connect("https://jouw.postnl.nl/#!/overzicht")
                    .cookies(loginCookies)
                    .get();
            log.info(doc.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // fetch data when logged in

    private Elements getAllDeliveryElements() {
        Optional<Document> optionalDoc = jsoupUtil.getPostNlDeliveriesDocument(email, password);
        if (optionalDoc.isPresent()) {
            return optionalDoc.get().select("div");
        }

        return new Elements();
    }

    private Set<Delivery> convertElementsToDeliveries(Elements elements) {
        return elements.stream()
                .filter(Objects::nonNull)
                .map(this::buildDelivery)
                .collect(Collectors.toSet());
    }

    private Delivery buildDelivery(Element element) {
        return Delivery.builder()
                .sender(getSender(element))
                .weightInGrams(getWeightInGrams(element))
                .startTime(getStartTime(element))
                .endTime(getEndTime(element))
                .build();
    }

    private String getSender(Element element) {
        return "";
    }

    private int getWeightInGrams(Element element) {
        return 0;
    }

    private LocalDateTime getStartTime(Element element) {
        return LocalDateTime.now();
    }


    private LocalDateTime getEndTime(Element element) {
        return LocalDateTime.now();
    }
}
