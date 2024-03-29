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

package selenium.webcrawler.model.CrawlResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.webcrawler.templates.MyLogger;
import selenium.webcrawler.model.JSON.JSONResultStructParser;

public class ResultParser {
    private WebDriver mDriver;
    private JSONResultStructParser mStructParser;
    private HashMap<String, String> mResults = new HashMap<>();

    public ResultParser(WebDriver driver, JSONResultStructParser structure) throws Exception {
        mDriver = driver;
        mStructParser = structure;

        try {
            // The root element must be identified by its id attribute
            final String expectedTopElemTag = mStructParser.getCurrentTag();
            final String expectedTopElemId = mStructParser.getCurrentAttributeValue("id");

            if (expectedTopElemId.equals("")) {
                throw new ParseException(0, "The JSON top object doesn't have any id attribute");
            }

            final WebElement topElem = mDriver.findElement(By.id(expectedTopElemId));
            if (!topElem.getTagName().equals(expectedTopElemTag)) {
                throw new ParseException(0, "The element found in the DOM, with the provided id, doesn't have the "
                    + "right tag name: " + topElem.getTagName() + " found instead of " + expectedTopElemTag + " expected");
            }

            parse(topElem, "/" + expectedTopElemTag, 0);
        } catch (ParseException pe) {
            throw new Exception("Exception while parsing the search result: " + pe);
        }
    }

    public HashMap<String, String> read() {
        return mResults;
    }

    private void parse(WebElement element, String path, int elementIndex) throws ParseException {
        MyLogger.log(Level.FINER, "Parsing: " + path);

        try {

            parseNode(element, path, elementIndex);

            walkChildren(element, path, elementIndex);

        } catch (ParseException e) {
            throw new ParseException(0, "Exception while parsing the result node at path `" + path 
                + "` and elementIndex `" + elementIndex + "`: " + e);
        }
    }

    private void parseNode(WebElement element, String path, int elementIndex) throws ParseException {
        MyLogger.log(Level.FINE, mStructParser.getCurrentInfo() + " was read from JSON");

        // Possibly update the path for multiple elements
        final String elementPath = mStructParser.isCurrentMultiple() ?
            (path + "/" + elementIndex) : path;

        // Check the element tag
        if (!element.getTagName().equals(mStructParser.getCurrentTag())) {
            throw new ParseException(0, "The child DOM element doesn't have the right tag name: "
                + element.getTagName() + " found instead of " + mStructParser.getCurrentTag() + " expected");
        }

        // Store or check the element attributes if any
        Map<String, JSONObject> expectedAttributes = mStructParser.getCurrentAttributes();

        for (JSONObject expectedAttribute: expectedAttributes.values()) {
            final var expectedAttributeKey = (String)expectedAttribute.get("key");
            if (expectedAttributeKey == null || expectedAttributeKey.equals("")) {
                throw new ParseException(0, "JSON node attribute has no key");
            }

            final var expectedAttributeValue = (String)expectedAttribute.get("value");
            if (expectedAttributeValue == null || expectedAttributeValue.equals("")) {
                throw new ParseException(0, "JSON node attribute has no value");
            }

            final String elementAttributeValue = element.getAttribute(expectedAttributeKey);
            if (elementAttributeValue == null || elementAttributeValue.equals("")) {
                throw new ParseException(0, "DOM node attribute has no value");
            }

            if (expectedAttributeValue.charAt(0) == '$') {
                // Store the DOM attribute value with the key: `<elementPath> + "/" +
                // <JSON attribute value without "$">`
                final String storingKey = expectedAttributeValue.substring(1);
                mResults.put(elementPath + "/" + storingKey, elementAttributeValue);
            } else {
                // Check if (<DOM attribute value> == <JSON attribute value>)

                if (!expectedAttributeValue.equals(elementAttributeValue)) {
                    throw new ParseException(0, "JSON and DOM nodes have different attribute values");
                }
            }
        }

        // Store the element value
        final String expectedValue = mStructParser.getCurrentValue();
        if (!expectedValue.equals("") && expectedValue.charAt(0) == '$') {
            // Store the DOM value with the key: `<elementPath> + "/" + <JSON value without "$">`
            final String storingKey = expectedValue.substring(1);
            final String elementValue = element.getText();

            mResults.put(elementPath + "/" + storingKey,
                (elementValue != null) ?
                    elementValue : ""
            );
        }

        MyLogger.log(Level.FINER, "Current result: \n" + getResultDigest());
    }

    private void walkChildren(WebElement element, String path, int elementIndex) throws ParseException {
        // Depth-first search for the children

        final int jSONNodeChildrenNumber = mStructParser.childrenNumber();

        if (jSONNodeChildrenNumber == 0) {
            // If no child in the JSON struct, don't parse the DOM children
            return;
        }

        final List<WebElement> dOMNodeChildren = element.findElements(By.xpath("./child::*"));
        final int dOMNodeChildrenNumber = dOMNodeChildren.size();

        // Possibly update the path for multiple elements
        final String elementPath = mStructParser.isCurrentMultiple() ?
            (path + "/" + elementIndex) : path;

        // Check if: (jSONNodeChildrenNumber <= dOMNodeChildrenNumber)
        if (jSONNodeChildrenNumber > dOMNodeChildrenNumber) {
            throw new ParseException(0, "JSON node cannot have more children than the related DOM node");
        }

        int jSONNodeChildIndex = 0, dOMNodeChildIndex = 0;
        JSONResultStructParser.State parentState = mStructParser.saveState();
        mStructParser.goToChild(0);

        while (dOMNodeChildIndex < dOMNodeChildrenNumber) {
            // 1 loop iteration compares a pair: (JSON node, DOM node).
            //
            // The following rules must be verified:
            // - If the current JSON node is multiple, it must be matched with 1..N DOM nodes, before
            // switching to the next JSON node.
            // - If the current JSON node is not multiple, it must be matched with 1 DOM node, before
            // switching to the next JSON node.

            WebElement dOMNodeChild = dOMNodeChildren.get(dOMNodeChildIndex);

            final boolean jSONNodeMatchedBeforeIteration = mStructParser.wasCurrentMatched();

            if (!mStructParser.isCurrentlyMatching(dOMNodeChild)) {
                if (!jSONNodeMatchedBeforeIteration) {
                    throw new ParseException(0, "The following JSON node didn't match at least once: "
                        + mStructParser.getCurrentInfo());
                }

                if (jSONNodeChildIndex == jSONNodeChildrenNumber - 1) {
                    MyLogger.log(Level.WARNING, "No more JSON node available to match the remaining DOM "
                        + "children nodes. Stop parsing them.");
                    break;
                }

                mStructParser.restoreState(parentState);
                mStructParser.goToChild(++jSONNodeChildIndex);

                continue;
            }

            if (jSONNodeMatchedBeforeIteration && !mStructParser.isCurrentMultiple()) {
                throw new ParseException(0, "JSON node matched more than once but is not multiple");
            }

            JSONResultStructParser.State childState = mStructParser.saveState();
            parse(dOMNodeChild, elementPath + "/" + mStructParser.getCurrentTag(), dOMNodeChildIndex);
            mStructParser.restoreState(childState);

            dOMNodeChildIndex++;
        }
    }

    private String getResultDigest() {
        StringBuilder res = new StringBuilder();

        for (String key: mResults.keySet()) {
            res.append(key + ": " + mResults.get(key) + "\n");
        }

        return res.toString();
    }
}
