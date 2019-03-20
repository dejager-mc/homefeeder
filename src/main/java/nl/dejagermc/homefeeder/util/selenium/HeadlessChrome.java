package nl.dejagermc.homefeeder.util.selenium;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class HeadlessChrome {
//    private static final String POSTNL_LOGIN_URI = "https://jouw.postnl.nl/?pst=k-pnl_f-f_p-pnl_u-txt_s-pwb_r-pnlinlogopties_v-jouwpost#!/inloggen?returnUrl=https:%2F%2Fwww.postnl.nl%2F";


    @Value("${webdriver.chrome.driver}")
    private String driverLocation;
//    @Value("${postnl.login.password}")
//    private String password;


    public HeadlessChrome() {
        // empty
    }

    public WebDriver getChromeWebdriver() {
        System.setProperty("webdriver.chrome.driver", driverLocation);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("window-size=2560x1440");
        return new ChromeDriver(options);
    }

//    public void main()  {
//        System.setProperty("webdriver.chrome.driver",
//                "/server/homefeeder/homefeeder/chrome/chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("headless");
//        options.addArguments("window-size=2560x1440");
//        WebDriver driver = new ChromeDriver(options);
//        driver.get(POSTNL_LOGIN_URI);
//
//        // cookies
//        WebElement cookies = driver.findElement(By.xpath("//*[@id=\"grantPermissionButton\"]"));
//        if (cookies != null) {
//            cookies.click();
//        }
//
////        takeScreenshot(driver, 1);
//
//        // login
//        driver.findElement(By.id("email")).sendKeys(email);
//        driver.findElement(By.id("password")).sendKeys(password);
//        driver.findElement(By.xpath("/html/body/div[3]/div/main/div[2]/ui-view/ui-view/ui-view/section/div/article/div/form/div/div[1]/div[5]/span/button")).click();
//
//
//        waitForLoading(driver);
//
//        takeScreenshot(driver, 2);
//
//
//        // overzicht
//        driver.get("https://jouw.postnl.nl/#!/overzicht");
//        // wait
//        waitForLoading(driver);
////        takeScreenshot(driver, 3);
//        driver.findElement(By.xpath("/html/body/div[3]/div/main/div[2]/ui-view/ui-view/ui-view/section/div/article/div[2]/div/div[1]/div[2]/div[1]/div[2]/a")).click();
//        waitForLoading(driver);
////        takeScreenshot(driver, 5);
//
////        List<WebElement> deliveres =  driver.findElements(By.xpath("/html/body/div[3]/div/main/div[2]/ui-view/ui-view/ui-view/section[1]/div/article/div/div[1]/div[1]/div[2]/div[3]/a"));
//        List<WebElement> deliveres =  driver.findElements(By.className("list-item-receiver"));
//
//        log.info("\n\nDeliveries:\n\n");
//        deliveres.forEach(el -> log.info(el.getText() + "\n\n"));
//
//        log.info("title is: " + driver.getTitle());
//        takeScreenshot(driver, 6);
//
//        driver.quit();
//    }
//
//    private void waitForLoading(WebDriver driver) {
//        try {
//            (new WebDriverWait(driver, 5))
//                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("testetefdgfgdhhfgfghfghdfgghfgd")));
//        } catch (Exception e) {
//            // meh
//        }
//    }
//
//    public void takeScreenshot(WebDriver driver, int count) {
//        ObjectIdGenerators.UUIDGenerator uuidGenerator = new ObjectIdGenerators.UUIDGenerator();
//
//        File scrFile = ((TakesScreenshot) driver)
//                .getScreenshotAs(OutputType.FILE);
//        try {
//            String file = "/server/homefeeder/homefeeder/chrome/" + count + "_" + uuidGenerator.generateId(this).toString() + ".png";
//            log.info("Saving screenshot to: {}", file);
//            FileUtils.copyFile(scrFile, new File(file));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void waitForLoad(WebDriver driver) {
//        ExpectedCondition<Boolean> pageLoadCondition = new
//                ExpectedCondition<Boolean>() {
//                    public Boolean apply(WebDriver driver) {
//                        return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
//                    }
//                };
//        WebDriverWait wait = new WebDriverWait(driver, 30);
//        wait.until(pageLoadCondition);
//    }
}
