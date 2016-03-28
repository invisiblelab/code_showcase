package com.sencha.gxt.test.sanity.misc;

import com.codeborne.selenide.WebDriverRunner;
import com.test.resources.base.driverSetup;
import com.test.resources.custom_profiles.IEnEventsOnCustom;
import com.test.resources.utils.misc;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.senchalabs.gwt.gwtdriver.gxt.models.Button;
import org.senchalabs.gwt.gwtdriver.models.GwtWidget;
import java.util.HashMap;
import java.util.List;
import static com.codeborne.selenide.Condition.appears;
import static com.codeborne.selenide.Selenide.$;

/**
 * @author - INVISIBLELAB
 * @name - Datepicker Test
 * @date 7.9.2015
 */

public class DatePickerTest extends driverSetup {

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
        gotoUrl("datepicker");
        misc.setScriptTimeout(driver, 15);
        misc.waitForExampleBody(driver);
    }

    private String result, year, month;

    /**
     * @param param1 - integer year
     * @param param2 - 3char name of month
     * @param driver - WebDriver
     * @method datePick() - selects month, day, year and checks if changes are reflected
     */

    private void datePick(Integer param1, String param2, WebDriver driver) throws InterruptedException {

        WebElement datepicker = driver.findElement(By.xpath(
                "//div[contains(@class, 'DatePickerStyle-datePicker')]"));

        int pos = misc.parsePickerData(datepicker);

        //get current year
        result = datepicker.getText().substring(0, pos);
        year = result.substring(result.length() - 4, result.length());
        month = result.substring(0, result.indexOf(" "));
        int yearParsed = Integer.parseInt(year);

        //click on month/year selection button
        datepicker.findElement(By.xpath(
                ".//div[contains(@class, 'DatePickerStyle-downIcon')]")).click();

        // wait for month picker effect
        $(driver.findElement(By.xpath(
                "//div[contains(@class, 'DatePickerStyle-monthPicker')]")))
                .waitUntil("Wait for monthPick effect", appears, 20);

        datepicker = driver.findElement(By.xpath(
                "//div[contains(@class, 'DatePickerStyle-monthPicker')]"));

        //find page with year wanted
        while (true) {
            try {

                datepicker.findElement(By.linkText(param1.toString()));
                break;

            } catch (org.openqa.selenium.NoSuchElementException e) {

                datepicker = driver.findElement(By.xpath(
                        "//div[contains(@class, 'DatePickerStyle-monthPicker')]"));

                if (param1 < yearParsed) {
                    if (System.getProperty("browserUnderTest").equals("firefox")) {

                        Thread.sleep(1000);

                        WebElement leftArrow = datepicker.findElement(By.xpath(
                                ".//div[contains(@class, 'DatePickerStyle-leftYearIcon')]"));

                        leftArrow.click();

                    } else {

                        Thread.sleep(1000);

                        WebElement leftArrow = datepicker.findElement(By.xpath(
                                ".//td[contains(@class, 'DatePickerStyle-yearButton')][1]"));

                        leftArrow.click();
                    }

                } else {
                    if (System.getProperty("browserUnderTest").equals("firefox")) {

                        Thread.sleep(1000);

                        WebElement rightArrow = datepicker.findElement(By.xpath(
                                "//div[contains(@class, 'DatePickerStyle-rightYearIcon')]"));

                        rightArrow.click();
                    } else {

                        Thread.sleep(1000);

                        WebElement rightArrow = datepicker.findElement(By.xpath(
                                ".//td[contains(@class, 'DatePickerStyle-yearButton')][2]"));

                        rightArrow.click();
                    }
                }
            }
        }

        datepicker = driver.findElement(By.xpath(
                "//div[contains(@class, 'DatePickerStyle-monthPicker')]"));

        WebElement yearToSelect = datepicker.findElement(By.linkText(param1.toString()));

        yearToSelect.click();

        int yearIsSelected = yearToSelect.findElement(
                By.xpath("./ancestor::td[1]")).getAttribute("class").indexOf("monthSelected");

        Assert.assertTrue("Year must be selected", yearIsSelected > -1);

        WebElement monthSelection = datepicker.findElement(By.linkText(param2));

        monthSelection.click();

        int mothIsSelected = monthSelection.findElement(
                By.xpath("./ancestor::td[1]")).getAttribute("class").indexOf("monthSelected");

        Assert.assertTrue("Month must be selected", mothIsSelected > -1);

        Button OK = GwtWidget.find(Button.class, driver).withText("OK").done();

        OK.click();

        datepicker = driver.findElement(By.xpath(
                "//div[contains(@class, 'DatePickerStyle-datePicker')]"));

        pos = misc.parsePickerData(datepicker);

        result = datepicker.getText().substring(0, pos);
        year = result.substring(result.length() - 4, result.length());
        month = result.substring(0, result.indexOf(" "));

        //parse data

        HashMap months = new HashMap();
        months.put("Jan", "January");
        months.put("Feb", "February");
        months.put("Mar", "March");
        months.put("Apr", "April");
        months.put("May", "May");
        months.put("Jun", "June");
        months.put("Jul", "July");
        months.put("Aug", "August");
        months.put("Sep", "September");
        months.put("Oct", "October");
        months.put("Nov", "November");
        months.put("Dec", "December");

        Assert.assertEquals("Currently selected year, must be same as param", param1.toString(), year);
        Assert.assertEquals("Currently selected month, must be same as param", months.get(param2), month);

        //Assert that year and month we wanted is selected
        WebElement pickerHeader = datepicker.findElement(
                By.xpath(".//td[contains(@class, 'DatePickerStyle-monthButtonText')]"));

        WebDriverWait wait = new WebDriverWait(driver, 25);
        wait.until(ExpectedConditions.textToBePresentInElement(
                pickerHeader, months.get(param2) + " " + param1));

        List<WebElement> cells = datepicker.findElements(By.xpath(
                "./div//td[contains(@class, 'DatePickerStyle-date')]"));

        //parse actual cells
        for (int i = 0; i < cells.size(); i++) {

            if (param1 < yearParsed) {

                int check = cells.get(i).getAttribute("class").indexOf("dateDisabled");
                Assert.assertTrue("Days should be disabled now", check > -1);

            } else {

                //various options in future
                int check = cells.get(i).getAttribute("class").indexOf("dateActive");
                int previous = cells.get(i).getAttribute("class").indexOf("datePrevious");
                int dateNext = cells.get(i).getAttribute("class").indexOf("dateNext");

                if (check == -1 && previous == -1 && dateNext == -1) {
                    Assert.fail("Date cells should be activated now");
                }

                WebElement day = driver.findElement(By.linkText("17"));
                day.click();

                Assert.assertTrue("Day must be selected by now",
                        day.findElement(By.xpath("./ancestor::td[1]"))
                                .getAttribute("class")
                                .indexOf("dateSelected") > -1);
                break;
            }
        }
    }

    @Test
    public void datePickMain() throws InterruptedException {

        datePick(2023, "Aug", driver);
        datePick(1965, "Feb", driver);
    }
}
