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

public class WebDriverTest {
    @Test
    public void testGoogleSearch() throws InterruptedException {

        final int pauseTimeInSec = 3;

        // Optional. If not specified, WebDriverTest searches the PATH for chromedriver.
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        final String searchPageUrl = "http://www.google.com/";

        final String searchBoxElementName = "q";

        final String searchCriteria = "New-York city";

        WebDriver driver = new ChromeDriver();
        driver.get(searchPageUrl);

        // Approve the consent
        WebElement consent = driver.findElement(By.xpath("//*[@id=\"L2AGLb\"]/div"));
        consent.click();

        // Fill in the search box
        WebElement searchBox = driver.findElement(By.name(searchBoxElementName));
        searchBox.sendKeys(searchCriteria);
        Thread.sleep(pauseTimeInSec * 1000);

        // Submit the search
        searchBox.submit();
        Thread.sleep(pauseTimeInSec * 1000);

        driver.quit();
    }
}
