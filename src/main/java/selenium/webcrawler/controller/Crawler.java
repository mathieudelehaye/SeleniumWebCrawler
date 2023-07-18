//
//  Crawler.java
//
//  Created by Mathieu Delehaye on 17/03/2023.
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

package selenium.webcrawler.controller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import selenium.webcrawler.model.CrawlResult.ResultParser;
import selenium.webcrawler.model.JSON.JSONResultStructParser;
import selenium.webcrawler.templates.Helpers;
import selenium.webcrawler.templates.MyLogger;
import java.io.FileReader;
import java.util.logging.Level;

public class Crawler {
    final private static int mPauseTimeInSec = 5;

    public void run() throws Exception {
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

        // parse the result, using the json file containing its structure
        var reader = new FileReader(ClassLoader.getSystemResource(structFilePath).getFile());
        var jsonParser = new JSONResultStructParser();
        jsonParser.init(reader);

        new ResultParser(driver, jsonParser);

        driver.quit();
    }
}
