package nl.dejagermc.homefeeder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
public class HomefeederApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomefeederApplication.class, args);
    }

}

