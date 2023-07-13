//
//  JSONResultStructParserTest.java
//
//  Created by Mathieu Delehaye on 12/07/2023.
//
//  SeleniumWebCrawler: An app to automatically use search engines and store the results to a database.
//
//  Copyright © 2023 Mathieu Delehaye. All rights reserved.
//
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
//  Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
//  any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
//  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
//  License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this program. If not, see
//  <https://www.gnu.org/licenses/>.

package selenium.webcrawler.model.JSON;

import java.util.logging.Level;
import org.junit.jupiter.api.*;
import selenium.webcrawler.MyLogger;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONResultStructParserTest {
    @BeforeAll
    static void beforeAll() {
        System.out.println("WebDriverTest: before all test methods");

        // Optional. If not specified, java.org.openqa.selenium.webcrawler.WebDriverTest searches the PATH for
        // chromedriver.
        System.setProperty("webdriver.chrome.driver",
            "/Volumes/portable-ssd/Web_Development/_J/chromedriver_mac64/chromedriver");

        MyLogger.setLevel(Level.FINER);
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("WebDriverTest: before each test method");
    }

    @AfterEach
    void afterEach() {
        System.out.println("WebDriverTest: after each test method");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("WebDriverTest: after all test methods");
    }

    @Test
    public void testStartFrom() {
        assertTrue(true);
    }

//    startFrom
//    goToChild
//    getCurrentTag
//    getCurrentAttributes
//    getCurrentAttribute
//    getCurrentAttributeValue
//    isCurrentMultiple
//    getCurrentInfo
}
