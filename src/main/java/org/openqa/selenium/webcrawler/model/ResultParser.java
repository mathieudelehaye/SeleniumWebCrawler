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

import java.util.Map;
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
    private ResultMap mResults = new ResultMap();

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
            final String topElemId = mStructParser.getCurrentAttributeValue("id");

            if (topElemId.equals("")) {
                throw new Exception("The JSON top object doesn't have any id attribute");
            }

            MyLogger.log(Level.INFO, mStructParser.getCurrentInfo() + " was read from JSON");

            final WebElement topElem = mDriver.findElement(By.id(topElemId));
            if (!topElem.getTagName().equals(topElemTag)) {
                throw new ParseException(0, "The element found in the DOM, with the provided id, doesn't have the "
                    + "right tag name: " + topElem.getTagName() + " found instead of " + topElemTag + " expected");
            }

            if (mStructParser.goToChild(0) != null) {
                parseChild(topElem, 0);
            }
        } catch (ParseException e) {
            throw new Exception("Exception while parsing the search result: " + e);
        }
    }

    private void parseChild(WebElement parentElem, int childIndex) throws Exception {

        try {
            MyLogger.log(Level.FINE, mStructParser.getCurrentInfo() + " was read from JSON");

            // Check if the tag of the DOM child element matches the current JSON node
            WebElement elem = parentElem.findElements(By.xpath("./child::*")).get(childIndex);

            if (!elem.getTagName().equals(mStructParser.getCurrentTag())) {
                throw new Exception("The child DOM element doesn't have the right tag name: "
                    + elem.getTagName() + " found instead of " + mStructParser.getCurrentTag() + " expected");
            }

            // If the tag is `ul`, add a nesting level
            if (mStructParser.getCurrentTag().equals("ul")) {
                mResults.increaseNesting();
            }

            if (mStructParser.getCurrentTag().equals("li")) {
                Map<String, JSONObject> attributes = mStructParser.getCurrentAttributes();

                for (JSONObject attribute: attributes.values()) {
                    final String attributeValue = (String)attribute.get("value");

                    if (attributeValue == null) {
                        throw new Exception("JSON node attribute has no value");
                    }

                    if (attributeValue.charAt(0) == '$') {
                        final String attributeKey = (String)attribute.get("key");

                        if (attributeKey == null) {
                            throw new Exception("JSON node attribute has no key");
                        }

                        final String elemAttributeValue = elem.getAttribute(attributeKey);
                        if (elem.getAttribute(attributeKey) != null) {
                            mResults.put(attributeKey, elemAttributeValue);
                        }
                    }
                }
            }

            MyLogger.log(Level.FINER, "Current result: " + mResults.getDigest());

            int i = 0;
            JSONObject parentNode;
            while (true) {
                parentNode = mStructParser.goToChild(i++);
                if (parentNode != null) {
                    parseChild(elem, i - 1);
                    mStructParser.startFrom(parentNode);
                } else {
                    break;
                }
            }
        } catch (ParseException e) {
            throw new Exception("Exception while parsing the search result: " + e);
        }
    }
}
