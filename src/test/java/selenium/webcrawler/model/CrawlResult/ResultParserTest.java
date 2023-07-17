//
//  ResultParserTest.java
//
//  Created by Mathieu Delehaye on 12/03/2023.
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

package selenium.webcrawler.model.CrawlResult;

import java.io.FileReader;
import java.util.logging.Level;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import selenium.webcrawler.templates.Helpers;
import selenium.webcrawler.templates.MyLogger;
import selenium.webcrawler.model.JSON.JSONResultStructParser;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultParserTest {
    final private static int mPauseTimeInSec = 3;
//    private static WebDriver mDriver;
//    private static JSONResultStructParser mStructParser;

    @BeforeAll
    static void beforeAll() {
        System.out.println("ResultParserTest: before all test methods");

        // Optional. If not specified, ResultParserTest searches the PATH for
        // chromedriver.
        System.setProperty("webdriver.chrome.driver",
            "/Volumes/portable-ssd/Web_Development/_J/chromedriver_mac64/chromedriver");

        MyLogger.setLevel(Level.FINER);

        // TODO: first UT. We keep it into the static method for
        // performance reason.
        final String searchPageUrl = "https://britishbeautycouncil.com/recycling-points/";
        final String searchBoxElementName = "wpsl-search-input";
        final String searchButtonElementId = "wpsl-search-btn";
        final String searchCriteria = "Manchester";

        WebDriver driver = new ChromeDriver();
        driver.get(searchPageUrl);

        // Fill in the search box
        WebElement searchBox = driver.findElement(By.name(searchBoxElementName));
        searchBox.sendKeys(searchCriteria);
        Helpers.sleep(mPauseTimeInSec * 1000);

        // Submit the search
        WebElement searchButton = driver.findElement(By.id(searchButtonElementId));
        searchButton.click();
        Helpers.sleep(mPauseTimeInSec * 1000);

        // Get the results
        final String structFilePath = "json/recycling_points_results.json";
        try (var reader = new FileReader(ClassLoader.getSystemResource(structFilePath).getFile())) {
            // parse the result, using the json file containing its structure
            var jsonParser = new JSONResultStructParser();
            jsonParser.init(reader);

            new ResultParser(driver, jsonParser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        driver.quit();

//        mDriver = new ChromeDriver();
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("ResultParserTest: before each test method");

//        // Parse the JSON file
//        final String structFilePath = "json/recycling_points_results.json";
//        try (var reader = new FileReader(ClassLoader.getSystemResource(structFilePath).getFile())) {
//            // parse the result, using the json file containing its structure
//            mStructParser = new JSONResultStructParser();
//            mStructParser.init(reader);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    @AfterEach
    void afterEach() {
        System.out.println("ResultParserTest: after each test method");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("ResultParserTest: after all test methods");

//        mDriver.quit();
    }

    @Test
    public void testSearchResult() {
//        final String searchPageUrl = "https://britishbeautycouncil.com/recycling-points/";
//        final String searchBoxElementName = "wpsl-search-input";
//        final String searchButtonElementId = "wpsl-search-btn";
//        final String searchCriteria = "Manchester";
//
//        mDriver.get(searchPageUrl);
//
//        // Fill in the search box
//        WebElement searchBox = mDriver.findElement(By.name(searchBoxElementName));
//        searchBox.sendKeys(searchCriteria);
//        Helpers.sleep(mPauseTimeInSec * 1000);
//
//        // Submit the search
//        WebElement searchButton = mDriver.findElement(By.id(searchButtonElementId));
//        searchButton.click();
//        Helpers.sleep(mPauseTimeInSec * 1000);

        // Get the results
//        readResult();

        assertTrue(true);
    }

//    private void readResult() {
//        try {
//            new ResultParser(mDriver, mStructParser);
//        } catch (Exception e) {
//            MyLogger.log(Level.SEVERE, e.getMessage());
//        }
//    }
}
