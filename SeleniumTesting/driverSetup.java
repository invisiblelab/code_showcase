package com.test.resources.base;

import com.codeborne.selenide.WebDriverRunner;
import com.test.resources.custom_profiles.InternetExplorerCustom;
import com.test.resources.rules.Retry;
import com.test.resources.custom_profiles.ChromeCustom;
import com.test.resources.custom_profiles.FirefoxCustom;
import com.test.resources.custom_profiles.SafariCustom;
import com.test.resources.utils.misc;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import java.util.logging.Logger;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * @CLASS driverSetup - tests base
 * @author INVISIBLELAB
 * @update: 15.1.2016
 */

public class driverSetup {

    private static final Logger logger = Logger.getLogger(driverSetup.class.getName());

    private static WebDriver last;
    @Rule
    public Retry retry = new Retry(3);
    public WebDriver driver;
    @Rule
    public TestWatcher quitFailedDriver = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            last.quit();
            last = null;
            driver = null;
        }
    };

    private void determinePlatform() {

        if (config.browserUnderTest.equals("chrome")) {

            if (System.getProperty("os.name").equals("Linux")) {
                //change path in the property if you need 32-bit chrome driver on linux
                System.setProperty("webdriver.chrome.driver",
                        "src/test/java/com/test/resources/drivers/chrome/linux64/chromedriver");
                return;
            } else if (System.getProperty("os.name").contains("Windows")) {
                System.setProperty("webdriver.chrome.driver",
                        "src/test/java/com/test/resources/drivers/chrome/win32/chromedriver.exe");
                return;
            } else if (System.getProperty("os.name").contains("Mac")) {
                System.setProperty("webdriver.chrome.driver",
                        "src/test/java/com/test/resources/drivers/chrome/mac32/chromedriver");
                return;
            }
        }
    }

    protected void gotoUrl(String address) {
        logger.info("gotoEntrypoint() + url=" + config.buildURL + address);
        String url = config.buildURL + address;
        driver.get(url);
    }

    private void createChromeCustom() {

        determinePlatform();

        ChromeCustom custom = new ChromeCustom();
        driver = custom.createDriver(DesiredCapabilities.chrome());
        WebDriverRunner.setWebDriver(driver);
    }

    private void createFirefoxCustom(){

        FirefoxCustom custom = new FirefoxCustom();
        driver = custom.createDriver(DesiredCapabilities.firefox());

        WebDriverRunner.setWebDriver(driver);
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    private void createSafariCustom(){

        SafariCustom custom = new SafariCustom();
        driver = custom.createDriver(DesiredCapabilities.safari());

        WebDriverRunner.setWebDriver(driver);
    }

    public void createIexploreCustom(){

        System.setProperty("webdriver.ie.driver",
                "src/test/java/com/test/resources/drivers/iexplore/IEx86server.exe");

        InternetExplorerCustom custom = new InternetExplorerCustom();
        driver = custom.createDriver(DesiredCapabilities.internetExplorer());

        WebDriverRunner.setWebDriver(driver);
        WebDriverRunner.getWebDriver().manage().window().maximize();

    }

    @Before
    public void setUpDriver() {

        if (last != null) {
            driver = last;
            last = null;
            return;
        }

        System.setProperty("browserUnderTest", config.browserUnderTest);
        System.setProperty("useCustomProfiles", config.useCustomProfiles);
        System.setProperty("remoteSetup", config.remoteSetup);

        if (System.getProperty("useCustomProfiles").equals("true")) {

            if (System.getProperty("browserUnderTest").equals("chrome")) {

                createChromeCustom();
            }

            if (System.getProperty("browserUnderTest").equals("firefox")) {

                createFirefoxCustom();
            }

            if (System.getProperty("browserUnderTest").equals("safari")) {

                createSafariCustom();
            }

            if (System.getProperty("browserUnderTest").equals("ie")) {

                createIexploreCustom();
            }

        } else if (System.getProperty("useCustomProfiles").equals("false")) {

            if (System.getProperty("browserUnderTest").equals("chrome")) {

                determinePlatform();

                WebDriverRunner.setWebDriver(new ChromeDriver());
                driver = WebDriverRunner.getWebDriver();
                driver.manage().window().maximize();
            }

            if (System.getProperty("browserUnderTest").equals("firefox")) {
                WebDriverRunner.setWebDriver(new FirefoxDriver());
                driver = WebDriverRunner.getWebDriver();
            }

            if (System.getProperty("browserUnderTest").equals("ie")) {

                //change filename if x64 is needed
                System.setProperty("webdriver.ie.driver",
                        "src/test/java/com/test/resources/drivers/iexplore/IEx86server.exe");

                WebDriverRunner.setWebDriver(new InternetExplorerDriver());
                driver = WebDriverRunner.getWebDriver();
            }

            if (System.getProperty("browserUnderTest").equals("safari")) {
                //instal safari driver plugin before instantiation, from safariDriver folder
                WebDriverRunner.setWebDriver(new SafariDriver());
                driver = WebDriverRunner.getWebDriver();
            }

        } else if (System.getProperty("remoteSetup").equals("true")) {
            //TODO
        }
    }

    @After
    public void stopBrowser() {

        misc.killAllTabs(driver);

        try {
            // make sure there is no alert present, failing the test
            Assert.assertTrue("Alert found when trying to close browser",
                    ExpectedConditions.not(ExpectedConditions.alertIsPresent()).apply(driver));

            last = driver;

        } catch (Exception ex) {
            driver.quit();
            last = null;
        }
    }
}