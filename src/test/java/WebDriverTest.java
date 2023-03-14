//
//  WebDriverTest.java
//
//  Created by Mathieu Delehaye on 12/03/2023.
//
//  BeautyAndroid: An Android app to order and recycle cosmetics.
//
//  Copyright © 2023 Mathieu Delehaye. All rights reserved.
//
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import java.util.List;

public class WebDriverTest {
    @Test
    public void testGoogleSearch() throws InterruptedException {

        final int pauseTimeInSec = 3;

        // Optional. If not specified, WebDriverTest searches the PATH for chromedriver.
        System.setProperty("webdriver.chrome.driver",
            "/Volumes/portable-ssd/Web_Development/_J/chromedriver_mac64/chromedriver");

        final String searchPageUrl = "https://britishbeautycouncil.com/recycling-points/";
        final String searchBoxElementName = "wpsl-search-input";
        final String searchButtonElementId = "wpsl-search-btn";
        final String searchCriteria = "Manchester";
        final String resultListDivElementId = "wpsl-stores";

        WebDriver driver = new ChromeDriver();
        driver.get(searchPageUrl);

        // Fill in the search box
        WebElement searchBox = driver.findElement(By.name(searchBoxElementName));
        searchBox.sendKeys(searchCriteria);
        Thread.sleep(pauseTimeInSec * 1000);

        // Submit the search
        WebElement searchButton = driver.findElement(By.id(searchButtonElementId));
        searchButton.click();
        Thread.sleep(pauseTimeInSec * 1000);

        // Get the results
        WebElement resulListDiv = driver.findElement(By.id(resultListDivElementId));

        WebElement resultListHead = resulListDiv.findElements(By.xpath("./child::*")).get(0);

        List<WebElement> resultListItems = resultListHead.findElements(By.xpath("./child::*"));
        final int resultCount =  resultListItems.size();
        System.out.println(resultCount);

        for (final WebElement listItem: resultListItems) {
            System.out.println("");

            final String resultId = listItem.getAttribute("data-store-id");
            System.out.println("id = " + resultId);

            final WebElement itemDiv = listItem.findElements(By.className("wpsl-store-location")).get(0);
            final WebElement divParagraph = itemDiv.findElements(By.xpath("./child::*")).get(0);

            final List<WebElement> itemLines = divParagraph.findElements(By.xpath("./child::*"));
            final int itemLineCount = itemLines.size();
            System.out.println("itemLineCount = " + itemLineCount);

            final String itemName = itemLines.get(0).getText();
            System.out.println("itemName = " + itemName);

            final List<WebElement> itemAddressLines = divParagraph.findElements(By.className("wpsl-street"));
            final int addressLineCount = itemAddressLines.size();
            System.out.println("addressLineCount = " + addressLineCount);

            for (final WebElement addressLinElement: itemAddressLines) {
                final String addressLine = addressLinElement.getText();
                System.out.println("addressLine = " + addressLine);
            }

            final List<WebElement> itemCityLines = itemLines.subList(1 + addressLineCount, itemLineCount);
            for (final WebElement itemCityLineElement: itemCityLines) {
                final String itemCityLine = itemCityLineElement.getText();
                System.out.println("itemCityLine = " + itemCityLine);
            }
        }

        driver.quit();
    }
}
