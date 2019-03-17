package nl.dejagermc.homefeeder.appconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableEurekaServer
public class HomefeederApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomefeederApplication.class, args);
    }

}