package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Slf4j
public class SeleniumWebDriver
{
    private static final WebDriver driver;
    private static int count = 0;
    static {
        System.setProperty("webdriver.chrome.driver", System.getenv("ChromeDriver"));
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver(chromeOptions);
    }

    public static String GetResponse(String url) {
        driver.get(url);
        WebElement html = driver.findElement(By.xpath("/html"));
        log.info(Integer.toString(++count));
        return html.getAttribute("outerHTML");
    }
}
