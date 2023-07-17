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
import java.util.Map;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.*;
import selenium.webcrawler.templates.MyLogger;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONResultStructParserTest {
    private static JSONResultStructParser mJsonParser;
    @BeforeAll
    static void beforeAll() {
        System.out.println("JSONResultStructParserTest: before all test methods");

        // Optional. If not specified, JSONResultStructParser searches the PATH for
        // chromedriver.
        System.setProperty("webdriver.chrome.driver",
            "/Volumes/portable-ssd/Web_Development/_J/chromedriver_mac64/chromedriver");

        MyLogger.setLevel(Level.FINER);
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("JSONResultStructParserTest: before each test method");

        // Parse the JSON file
        final String structFilePath = "json/struct_description_simple.json";
        try (var reader = new FileReader(ClassLoader.getSystemResource(structFilePath).getFile())) {
            mJsonParser = new JSONResultStructParser();
            mJsonParser.init(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
    public void testReadNodeTags() {
        assertTrue(readTag().equals("div"));
        assertTrue(mJsonParser.childrenNumber() == 1);

        final JSONObject parent = mJsonParser.goToChild(0);
        assertTrue(mJsonParser.childrenNumber() == 1);
        assertTrue(readTag().equals("ul"));

        mJsonParser.startFrom(parent);
        assertTrue(readTag().equals("div"));
    }

    @Test
    public void testReadNodeAttributes() {
        mJsonParser.goToChild(0); // return value = <div> node
        mJsonParser.goToChild(0); // return value = <ul> node

        final Map<String, JSONObject> attributes = readAttributes();

        final JSONObject attribute1 = attributes.get("list-item-id");
        if (attribute1 != null) {
            assertTrue(attribute1.get("key").equals("list-item-id"));
            assertTrue(attribute1.get("value").equals("$list_item_id"));
        } else {
            assertTrue(false);
        }

        final JSONObject attribute2 = attributes.get("list-item-date");
        if (attribute1 != null) {
            assertTrue(attribute2.get("key").equals("list-item-date"));
            assertTrue(attribute2.get("value").equals("$list_item_date"));
        } else {
            assertTrue(false);
        }
    }

    @Test
    public void testReadNodeAttribute() {
        mJsonParser.goToChild(0); // return value = <div> node

        final JSONObject attribute = readAttribute("foo");
        if (attribute != null) {
            assertTrue(attribute.get("key").equals("foo"));
            assertTrue(attribute.get("value").equals("unordered-list-bar"));
        } else {
            assertTrue(false);
        }
    }

    @Test
    public void testReadNodeAttributeValue() {
        mJsonParser.goToChild(0); // return value = <div> node
        mJsonParser.goToChild(0); // return value = <ul> node
        mJsonParser.goToChild(0); // return value = <li> node
        mJsonParser.goToChild(1); // return value = <p> node

        final String value = readAttributeValue("class");
        if (value != null) {
            assertTrue(value.equals("span-class-01"));
        } else {
            assertTrue(false);
        }
    }

    @Test
    public void testReadNodeValues() {
        assertTrue(mJsonParser.getCurrentValue().equals(""));

        mJsonParser.goToChild(0); // return value = <div> node
        assertTrue(mJsonParser.getCurrentValue().equals(""));

        mJsonParser.goToChild(0); // return value = <ul> node
        assertTrue(mJsonParser.getCurrentValue().equals(""));

        mJsonParser.goToChild(0); // return value = <li> node
        assertTrue(mJsonParser.getCurrentValue().equals(""));

        final JSONObject parent = mJsonParser.goToChild(0); // return value = <p> node
        assertTrue(mJsonParser.getCurrentValue().equals("$content_title"));

        mJsonParser.startFrom(parent);
        mJsonParser.goToChild(1); // return value = <p> node
        assertTrue(mJsonParser.getCurrentValue().equals("$content_description"));

        mJsonParser.startFrom(parent);
        mJsonParser.goToChild(2); // return value = <p> node
        assertTrue(mJsonParser.getCurrentValue().equals(""));
    }

    @Test
    public void testReadNodeMultiplicity() {
        assertTrue(mJsonParser.isCurrentMultiple() == false);

        mJsonParser.goToChild(0); // return value = <div> node
        assertTrue(mJsonParser.isCurrentMultiple() == false);

        mJsonParser.goToChild(0); // return value = <ul> node
        assertTrue(mJsonParser.isCurrentMultiple() == false);

        mJsonParser.goToChild(0); // return value = <li> node
        assertTrue(mJsonParser.isCurrentMultiple() == false);

        mJsonParser.goToChild(0); // return value = <p> node
        assertTrue(mJsonParser.isCurrentMultiple() == true);
    }

    @Test
    public void testReadNodeInfo() {
        mJsonParser.goToChild(0); // return value = <div> node
        mJsonParser.goToChild(0); // return value = <ul> node
        mJsonParser.goToChild(0); // return value = <li> node
        mJsonParser.goToChild(1); // return value = <p> node

        final String info = readInfo();

        assertTrue(info != null);
        assertTrue(readInfo().equals("Expected node with tag `span`, no id, class `span-class-01`"));
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

    private Map<String, JSONObject> readAttributes() {
        try {
            final Map<String, JSONObject> attributes = mJsonParser.getCurrentAttributes();
            return attributes;
        } catch (ParseException pe) {
            MyLogger.log(Level.SEVERE, pe.getMessage());
            return null;
        }
    }

    private JSONObject readAttribute(String key) {
        try {
            final JSONObject attribute = mJsonParser.getCurrentAttribute(key);
            return attribute;
        } catch (ParseException pe) {
            MyLogger.log(Level.SEVERE, pe.getMessage());
            return null;
        }
    }

    private String readAttributeValue(String key) {
        try {
            final String value = mJsonParser.getCurrentAttributeValue(key);
            return value;
        } catch (ParseException pe) {
            MyLogger.log(Level.SEVERE, pe.getMessage());
            return null;
        }
    }

    private String readInfo() {
        try {
            final String info = mJsonParser.getCurrentInfo();
            return info;
        } catch (ParseException pe) {
            MyLogger.log(Level.SEVERE, pe.getMessage());
            return null;
        }
    }
}
