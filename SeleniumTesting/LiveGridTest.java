package com.sencha.gxt.test.sanity.grids;

import com.codeborne.selenide.WebDriverRunner;
import com.test.resources.base.driverSetup;
import com.test.resources.custom_profiles.IEnEventsOnCustom;
import com.test.resources.utils.misc;
import com.test.resources.utils.grid;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.util.List;

/**
 * @author INVISIBLELAB
 * @name Livegrid Test
 * @update: 8.1.2016
 * Tested on: Firefox 43.0.4, Chrome 47.0.2526.106, IE10 - 10.0.9200.17457
 * works
 */

public class LiveGridTest extends driverSetup {

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
        gotoUrl("livegrid");
        misc.setScriptTimeout(driver, 25);
        misc.waitForExampleBody(driver);
    }

    private String defaultCssClass;
    private List<WebElement> rows;

    private void assertSelected(int... index) {
        for (int sel : index) {
            WebElement selectedItem = rows.get(sel);
            String selectedCssClass = selectedItem.getAttribute("class");
            Assert.assertTrue(defaultCssClass != selectedCssClass);
        }
    }

    /**
     * LiveGridScrollingTest - tries to scroll live grid, row display state
     *
     * @throws InterruptedException
     */

    @Test
    public void LiveGridScrollingTest() throws InterruptedException {

        WebElement gridElement = grid.refreshGridReference(driver);
        rows = gridElement.findElements(By.xpath(".//tr[contains(@class, 'GridStyle-row')]"));

        //check rows are loaded correctly (previous loading problems)
        //if loaded click on row to begin scrolling
        if (rows.size() > 1) {
            defaultCssClass = rows.get(1).getAttribute("class");

            if (rows.get(1).isDisplayed()) {
                rows.get(1).click();
            }

            //scroll to death
            //known bug at row 151, let it crash
            for (int i = 0; i < 200; i++) {

                WebElement panel = misc.returnPanel(driver, "Live Grid").getElement();

                //get current displayed results
                String toolbarText = panel.findElement(By.xpath(".//div[contains(@class," +
                        " 'ToolBarStyle-toolBar')]")).getText();

                //test scrolling of rows
                try {
                    new Actions(driver).sendKeys( Keys.ARROW_DOWN).build().perform();

                } catch (org.openqa.selenium.UnhandledAlertException e){
                    Assert.fail("EXTGWT-3980 - JS Exception");
                }

                //for first 7 check css effect row is selected
                if (i < 7) {

                    for (int x = 0; x<rows.size(); x++){
                        rows = gridElement.findElements(By.xpath(".//tr[contains(@class, 'GridStyle-row')]"));
                        assertSelected(x);
                    }
                }

                //if row is bigger than 12 and "page changed" (rows.size()- rows per page) - change with display res.
                //then test toolbar text showing current displayed state
                if ((i >= rows.size()) && (i % rows.size() == 0)){
                    panel = misc.returnPanel(driver, "Live Grid").getElement();

                    //get current displayed results
                    String toolbarText2 = panel.findElement(By.xpath(".//div[contains(@class," +
                            " 'ToolBarStyle-toolBar')]")).getText();

                    Assert.assertNotEquals("Info about displayed results should change.", toolbarText, toolbarText2);
                }
            }

        } else {
            Assert.assertTrue("Rows are not loaded correctly", rows.size() > 1);
        }
    }
}
