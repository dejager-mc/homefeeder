package nl.dejagermc.homefeeder.input.postnl.repository;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.postnl.model.Delivery;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
import nl.dejagermc.homefeeder.util.selenium.HeadlessChrome;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DeliveryRepository {
    private static final String POSTNL_LOGIN_URI = "https://jouw.postnl.nl/?pst=k-pnl_f-f_p-pnl_u-txt_s-pwb_r-pnlinlogopties_v-jouwpost#!/inloggen?returnUrl=https:%2F%2Fwww.postnl.nl%2F";
    private static final String POSTNL_DELIVERIES_LIST = "https://jouw.postnl.nl/#!/overzicht";
    private static final int PAGE_LOAD_WAIT_TIMEOUT = 3;

    private Set<Delivery> savedDeliveries = new HashSet<>();

    @Value("${postnl.login.email}")
    private String email;
    @Value("${postnl.login.password}")
    private String password;

    private HeadlessChrome headlessChrome;

    @Autowired
    public DeliveryRepository(HeadlessChrome headlessChrome) {
        this.headlessChrome = headlessChrome;
    }

    public void addSavedDelivery(Delivery delivery) {
        this.savedDeliveries.add(delivery);
    }

    public List<Delivery> getSavedDeliveries() {
        return new ArrayList<>(savedDeliveries);
    }

    public void resetSavedDeliveries() {
        savedDeliveries = new HashSet<>();
    }

    @Cacheable(cacheNames = "getAllDeliveries", cacheManager = "cacheManagerCaffeine")
    public Set<Delivery> getAllDeliveries() {
        log.info("UC600: get all deliveries.");

        // get webdriver
        WebDriver webDriver = headlessChrome.getChromeWebdriver();

        // log in to postnl
        loginPostnl(webDriver);

        // open deliveries page and expand it
        openDeliveriesPage(webDriver);

        // get deliveries from page
        log.info("UC600: getting list of deliveries");
        List<WebElement> deliveres =  webDriver.findElements(By.className("list-item-receiver"));

        // convert to delivery objects
        Set<Delivery> results = deliveres.stream().map(this::buildDelivery).collect(Collectors.toSet());

        // close driver
        webDriver.close();

        log.info("UC600: returning {} deliveries.", results.size());
        return results;
    }

    private void loginPostnl(WebDriver driver) {
        log.info("UC600: loging in to postnl.");
        driver.get(POSTNL_LOGIN_URI);
        waitForPageLoading(driver);

        // accept cookies if requested
        WebElement cookies = driver.findElement(By.xpath("//*[@id=\"grantPermissionButton\"]"));
        if (cookies != null) {
            cookies.click();
        }

        // login
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.xpath("/html/body/div[3]/div/main/div[2]/ui-view/ui-view/ui-view/section/div/article/div/form/div/div[1]/div[5]/span/button")).click();
        waitForPageLoading(driver);
    }

    private void openDeliveriesPage(WebDriver driver) {
        log.info("UC600: opening page with deliveries.");
        // open page
        driver.get(POSTNL_DELIVERIES_LIST);
        waitForPageLoading(driver);
        waitForPageLoading(driver);

        // expand
        driver.findElement(By.xpath("/html/body/div[3]/div/main/div[2]/ui-view/ui-view/ui-view/section/div/article/div[2]/div/div[1]/div[2]/div[1]/div[2]/a")).click();
        waitForPageLoading(driver);
    }

    private void waitForPageLoading(WebDriver driver) {
        try {
            new WebDriverWait(driver, PAGE_LOAD_WAIT_TIMEOUT)
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("testetefdgfgdhhfgfghfghdfgghfgd")));
        } catch (Exception e) {}
    }

    private Delivery buildDelivery(WebElement webElement) {
        List<String> infoLines = List.of(webElement.getText().split("\n"));
        return Delivery.builder()
                .sender(infoLines.get(0))
                .hasBeenDelivered(setHasBeenDelivered(infoLines.get(1)))
                .build();
    }

    public boolean setHasBeenDelivered(String string) {
        if (string.matches("Bezorgd op.*")) {
            return true;
        }
        return false;
    }
}
