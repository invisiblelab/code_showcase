package com.sencha.gxt.test.sanity.dnd;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebDriverRunner;
import com.test.resources.base.driverSetup;
import com.test.resources.custom_profiles.IEnEventsOnCustom;
import com.test.resources.utils.misc;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.util.List;
import static com.codeborne.selenide.Selenide.$$;

/**
 * @author INVISIBLELAB
 * @name Grid To Grid - Test
 * @update 15.1.2016
 */

public class GridToGridDNDTest extends driverSetup {

    protected List<WebElement> gridElements, rows1, rows2;

    @Override
    public void createIexploreCustom() {

        System.setProperty("webdriver.ie.driver",
                "src/test/java/com/test/resources/drivers/iexplore/IEx86server.exe");

        //enable native events
        IEnEventsOnCustom custom = new IEnEventsOnCustom();
        driver = custom.createDriver(DesiredCapabilities.internetExplorer());

        WebDriverRunner.setWebDriver(driver);
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    @Before
    public void setUp() {
        gotoUrl("gridtogrid");
        misc.setScriptTimeout(driver, 25);
        misc.waitForExampleBody(driver);

        gridElements = driver.findElements(By.xpath("//div[contains(@class," +
                " 'Css3GridAppearance" + "-GridStyle-grid')]"));

        rows1 = gridElements.get(1).findElements(By.xpath(".//tr[contains(@class," +
                " 'GridStyle-row')]"));

        rows2 = gridElements.get(3).findElements(By.xpath(".//tr[contains(@class," +
                " 'GridStyle-row')]"));

    }

    @Test
    public void appendGridTest() {

        //order of the grid is over-turned
        WebElement appendTarget = gridElements.get(0);

        for (int i = 0; i < 10; i++) {
            int loc = rows1.get(i).getLocation().getX();

            new Actions(driver).dragAndDrop(rows1.get(i), appendTarget).build().perform();

            rows1 = driver.findElements(By.xpath("//tr[contains(@class," +
                    " 'GridStateStyles-row')]"));

            int loc2 = rows1.get(i).getLocation().getX();

            Assert.assertNotEquals("Row should be dropped in second grid.", loc, loc2);
        }

        gridElements = driver.findElements(By.xpath("//div[contains(@class," +
                " 'Css3GridAppearance" + "-GridStyle-grid')]"));

        ElementsCollection verify = $$(gridElements.get(0).findElements(By.xpath(
                ".//tr[contains(@class," + " 'GridStyle-row')]")));

        verify.shouldHaveSize(10);
    }

    @Test
    public void gridInsert() {

        WebElement insertTarget = gridElements.get(2);

        for (int i = 0; i < 10; i++) {

            String rowText = rows2.get(i).getText();

            new Actions(driver).dragAndDrop(rows2.get(i), insertTarget).build().perform();

            //insertion test only if grid has at least 3 items
            //it inserts into the middle of element
            if (i > 3) {

                gridElements = driver.findElements(By.xpath("//div[contains(@class," +
                        " 'Css3GridAppearance" + "-GridStyle-grid')]"));

                insertTarget = gridElements.get(2);

                String rowText2 = insertTarget.findElement(
                        By.xpath("./div[2]/div/table/tbody/tr[3]/td")).getText();

                Assert.assertEquals("Strings should be equal", rowText, rowText2);

                gridElements = driver.findElements(By.xpath("//div[contains(@class," +
                        " 'Css3GridAppearance" + "-GridStyle-grid')]"));
            }
        }

        ElementsCollection verify = $$(insertTarget.findElements(By.xpath(
                ".//tr[contains(@class, 'GridStyle-row')]")));

        //assert that elements are dropped in second grid
        verify.shouldHaveSize(10);
    }
}
