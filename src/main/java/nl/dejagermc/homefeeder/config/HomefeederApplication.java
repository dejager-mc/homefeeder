package nl.dejagermc.homefeeder.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class HomefeederApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomefeederApplication.class, args);
    }

}