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

import java.io.FileReader;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.*;
import selenium.webcrawler.MyLogger;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONResultStructParserTest {
    private static JSONResultStructParser mJsonParser;
    @BeforeAll
    static void beforeAll() {
        System.out.println("JSONResultStructParserTest: before all test methods");

        // Optional. If not specified, java.org.openqa.selenium.webcrawler.WebDriverTest searches the PATH for
        // chromedriver.
        System.setProperty("webdriver.chrome.driver",
            "/Volumes/portable-ssd/Web_Development/_J/chromedriver_mac64/chromedriver");

        MyLogger.setLevel(Level.FINER);

        // Parse the JSON file
        final String structFilePath = "json/struct_description_simple.json";
        try (var reader = new FileReader(ClassLoader.getSystemResource(structFilePath).getFile())) {
            mJsonParser = new JSONResultStructParser();
            mJsonParser.init(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("JSONResultStructParserTest: before each test method");
    }

    @AfterEach
    void afterEach() {
        System.out.println("JSONResultStructParserTest: after each test method");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("JSONResultStructParserTest: after all test methods");
    }

    @Test
    public void testReadTags() {
        MyLogger.log(Level.INFO, readTag());
        assertTrue(readTag().equals("div"));

        final JSONObject parent = mJsonParser.goToChild(0);
        MyLogger.log(Level.INFO, readTag());
        assertTrue(readTag().equals("ul"));

        mJsonParser.startFrom(parent);
        MyLogger.log(Level.INFO, readTag());
        assertTrue(readTag().equals("div"));
    }

    private String readTag() {
        try {
            final String tag = mJsonParser.getCurrentTag();
            return tag;
        } catch (ParseException pe) {
            MyLogger.log(Level.SEVERE, pe.getMessage());
            return null;
        }
    }

//    getCurrentAttributes
//    getCurrentAttribute
//    getCurrentAttributeValue
//    isCurrentMultiple
//    getCurrentInfo
}
