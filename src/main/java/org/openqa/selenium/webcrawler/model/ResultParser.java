//
//  ResultParser.java
//
//  Created by Mathieu Delehaye on 15/03/2023.
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
//  <https://www.gnu.org/licenses/>.ses/>.

package org.openqa.selenium.webcrawler.model;

import java.util.*;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.webcrawler.MyLogger;

public class ResultParser {
    private WebDriver mDriver;
    private JSONResultStructParser mStructParser;
    private Map<String, Object> mResults = new HashMap<>();

    // TODO: remove and parse it from the JSON struct file
    final String resultIdAttribute = "data-store-id";
    final String resultDivClass = "wpsl-store-location";
    final String resultAddressSpanClass = "wpsl-street";

    public ResultParser(WebDriver driver, JSONResultStructParser structure) throws Exception {
        mDriver = driver;
        mStructParser = structure;

        // The root element will generally be identified by its id attribute:
        try {
            final String topElemTag = mStructParser.getCurrentTag();
            JSONObject idAttribute = mStructParser.getCurrentAttribute("id");

            if (idAttribute == null) {
                throw new Exception("The JSON top object doesn't have any id attribute");
            }

            final String topElemId = (String) idAttribute.get("value");

            MyLogger.setLevel(Level.FINER);
            MyLogger.log(Level.INFO, "Read the result element tag `{0}` and id `{1}` from JSON",
                topElemTag, topElemId);

            final WebElement topElem = mDriver.findElement(By.id(topElemId));

            if (!topElem.getTagName().equals(topElemTag)) {
                throw new ParseException(0, "The element found in the DOM, with the provided id, doesn't have the "
                    + "correct tag name: " + topElem.getTagName() + " found instead of " + topElemTag + " expected");
            }

            if(mStructParser.goToChild(0) != null) {
                parse();
            }
        } catch (ParseException e) {
            throw new Exception("Exception while parsing the search result: " + e);
        }
    }

    private void parse() throws Exception {

        // The root element will generally be identified by its id attribute:
        try {
            final String tag = mStructParser.getCurrentTag();
            final String id = mStructParser.getCurrentAttributeValue("id");
            final String elemClass = mStructParser.getCurrentAttributeValue("class");

            MyLogger.setLevel(Level.FINER);
            MyLogger.log(Level.INFO, "Read the result element tag `{0}`, with {1} and {2} from JSON",
                tag,
                !id.equals("") ? ("id " + id) : "no id",
                !elemClass.equals("") ? ("class " + elemClass) : "no class");

            int i = 0;
            JSONObject parent;
            while (true) {
                parent = mStructParser.goToChild(i++);
                if (parent != null) {
                    parse();
                    mStructParser.setCurrent(parent);
                } else {
                    break;
                }
            }
        } catch (ParseException e) {
            throw new Exception("Exception while parsing the search result: " + e);
        }
    }
}
