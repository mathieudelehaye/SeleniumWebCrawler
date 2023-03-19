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
import java.io.FileReader;
import java.util.Iterator;

public class JSONResultStructParser extends JSONParser {
    private JSONObject mRoot;
    private JSONObject mCurrent;

    public JSONObject parse(FileReader reader) throws ParseException {
        try {
            mRoot = (JSONObject) super.parse(reader);
            mCurrent = mRoot;
        } catch (Exception e) {
            throw new ParseException(0, "Error while parsing the file reader: " + e.getMessage());
        }

        return mRoot;
    }

    String getCurrentAttribute(String key) throws ParseException {
        var attributeArray = (JSONArray) mCurrent.get("attributes");

        if (attributeArray == null) {
            throw new ParseException(0, "Current JSON object has no `attributes` field, while trying to read it");
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

                return attributeValue;
            }
        }

        throw new ParseException(0, "No attribute found with the given key for the current object");
    }
}
