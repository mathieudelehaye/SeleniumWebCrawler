//
//  JSONResultStructParser.java
//
//  Created by Mathieu Delehaye on 19/03/2023.
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.webcrawler.MyLogger;
import java.io.FileReader;
import java.util.Iterator;
import java.util.logging.Level;

public class JSONResultStructParser extends JSONParser {
    private JSONObject mRoot;
    private JSONObject mCurrent;

    public void setCurrent(JSONObject node) {
        mCurrent = node;
    }

    public JSONObject init(FileReader reader) throws ParseException {
        try {
            mRoot = (JSONObject) super.parse(reader);
            mCurrent = mRoot;
        } catch (Exception e) {
            throw new ParseException(0, "Error while parsing the file reader: " + e);
        }

        return mRoot;
    }

    public JSONObject goToChild(int index) {
        var childrenArray = (JSONArray) mCurrent.get("children");

        if (childrenArray == null) {
            MyLogger.log(Level.FINE, "Current JSON object has no `children` field, while trying to read it");
            return null;
        }

        final Iterator attribute = childrenArray.iterator();
        JSONObject innerObj = (JSONObject)attribute.next();

        for (int i = 0; i < index; i++) {
            if (attribute.hasNext()) {
                innerObj = (JSONObject)attribute.next();
            } else {
                return null;
            }
        }

        if (innerObj != null) {
            JSONObject output = mCurrent;
            mCurrent = innerObj;

            // Return the parent node, so it can be saved by the class user.
            return output;
        }

        return null;
    }

    public String getCurrentTag() throws ParseException {
        var tag = (String) mCurrent.get("tag");

        if (tag == null) {
            throw new ParseException(0, "Current object has no `tag` field");
        }

        return tag;
    }

    public JSONObject getCurrentAttribute(String key) throws ParseException {
        var attributeArray = (JSONArray) mCurrent.get("attributes");

        if (attributeArray == null) {
            //MyLogger.log(Level.FINE, "Current JSON object has no `attributes` field, while trying to read it");
            return null;
        }

        final Iterator attribute = attributeArray.iterator();

        String attributeValue;
        while (attribute.hasNext()) {
            var innerObj = (JSONObject) attribute.next();
            var attributeKey = (String)innerObj.get("key");

            if (attributeKey == null) {
                throw new ParseException(0, "Current `attributes` item has no `key` field");
            }

            if (attributeKey.equals(key)) {
                attributeValue = (String)innerObj.get("value");

                if (attributeValue == null) {
                    throw new ParseException(0, "Current `attributes` item has no `value` field, "
                        + "while trying to read it");
                }

                return innerObj;
            }
        }

        //MyLogger.log(Level.FINE, "No attribute found with the given key for the current object");
        return null;
    }

    public String getCurrentAttributeValue(String key) throws ParseException {
        JSONObject attribute = getCurrentAttribute(key);

        if (attribute != null) {
            return (String) attribute.get("value");
        } else {
            return "";
        }
    }
}
