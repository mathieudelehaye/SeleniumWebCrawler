//
//  ResultMapTest.java
//
//  Created by Mathieu Delehaye on 15/07/2023.
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

import java.util.Map;
import java.util.logging.Level;
import org.junit.jupiter.api.*;
import selenium.webcrawler.templates.MyLogger;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultMapTest {
    private ResultMap mResults;

    @BeforeAll
    static void beforeAll() {
        System.out.println("ResultMapTest: before all test methods");

        // Optional. If not specified, ResultParserTest searches the PATH for
        // chromedriver.
        System.setProperty("webdriver.chrome.driver",
            "/Volumes/portable-ssd/Web_Development/_J/chromedriver_mac64/chromedriver");

        MyLogger.setLevel(Level.FINER);
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("ResultMapTest: before each test method");

        mResults = new ResultMap();

        mResults.increaseNesting("ul");

        mResults.increaseNesting("li");
        mResults.put("list_item_id", "26340");
        mResults.put("list_item_date", "23-07-16");

        mResults.increaseNesting("p");

        Map<String, Object> parent = mResults.increaseNesting("strong");
        mResults.put("content_title", "Body Shop (Manchester Royal Exchange)");

        mResults.changeNodeTo(parent);
        mResults.increaseNesting("span");
        mResults.put("content_description", "Royal Exchange Shopping Centre");
    }

    @AfterEach
    void afterEach() {
        System.out.println("ResultMapTest: after each test method");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("ResultMapTest: after all test methods");
    }

    @Test
    public void testFindData() {

        assertTrue(mResults.find("list_item_id", true).equals("26340"));
    }

//    getDigest
//    searchFrom
}
