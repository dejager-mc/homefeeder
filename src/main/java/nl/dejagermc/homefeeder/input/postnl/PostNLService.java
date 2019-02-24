package nl.dejagermc.homefeeder.input.postnl;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.postnl.model.Delivery;
import nl.dejagermc.homefeeder.input.postnl.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.postnl.predicates.DeliveryPredicates.isDeliveryForToday;

@Slf4j
@Service
public class PostNLService {
    private DeliveryRepository deliveryRepository;

    @Autowired
    public PostNLService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public Set<Delivery> getAllDeliveries() {
        return deliveryRepository.getAllDeliveries();
    }

    public Set<Delivery> getTodaysDeliveries() {
        return deliveryRepository.getAllDeliveries().stream()
                .filter(isDeliveryForToday())
                .collect(Collectors.toSet());
    }

//    public void test() {
//        try {
//            //With this you login and a session is created
//            Connection.Response res = Jsoup.connect("https://jouw.postnl.nl/?pst=k-pnl_f-f_p-pnl_u-txt_s-pwb_r-pnlinlogopties_v-jouwpost#!/inloggen")
//                    .data("email", email, "password", password)
//                    .method(Connection.Method.POST)
//                    .execute();
//
//            //This will get you cookies
//            Map<String, String> loginCookies = res.cookies();
//
//            //Here you parse the page that you want. Put the url that you see when you have logged in
//            Document doc = Jsoup.connect("https://jouw.postnl.nl/?pst=k-pnl_f-f_p-pnl_u-txt_s-pwb_r-pnlinlogopties_v-jouwpost#!/overzicht")
//                    .cookies(loginCookies)
//                    .get();
//            log.info(doc.toString());
//        } catch (Exception e) {
//            log.error("Exception postnl: ", e);
//        }
//    }
}
